package com.aurea.deadcode.service.impl;

import com.aurea.deadcode.exception.MalformedExpressionException;
import com.aurea.deadcode.model.Antipattern;
import com.aurea.deadcode.model.Occurrence;
import com.aurea.deadcode.model.PageLabel;
import com.aurea.deadcode.repository.OccurrenceRepository;
import com.aurea.deadcode.repository.PageLabelRepository;
import com.aurea.deadcode.service.OccurrenceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by ekonovalov on 10.03.2017.
 */
@Slf4j
@Service
public class OccurrenceServiceImpl implements OccurrenceService {

    private static final JexlEngine JEXL_ENGINE = new JexlBuilder().create();

    private final OccurrenceRepository occurrenceRepository;
    private final PageLabelRepository pageLabelRepository;

    @Value("${occurrence.limit}")
    private Integer limit = 50;

    @Autowired
    public OccurrenceServiceImpl(OccurrenceRepository occurrenceRepository,
                                 PageLabelRepository pageLabelRepository) {

        this.occurrenceRepository = occurrenceRepository;
        this.pageLabelRepository = pageLabelRepository;
    }

    @Override
    public List<Occurrence> getByRepositoryId(Long repositoryId) {
        return occurrenceRepository.findByRepositoryIdAndAntipattern(repositoryId, Antipattern.DEAD_CODE);
    }

    @Override
    public Page<Occurrence> getByRepositoryId(Long repositoryId, Integer limit, Integer page) {
        return occurrenceRepository.findByRepositoryIdAndAntipatternOrderById(repositoryId, Antipattern.DEAD_CODE, new PageRequest(page, limit));
    }

    @Override
    @Transactional
    public Page<Occurrence> getByRepositoryId(Long repositoryId, Integer limit, Integer page, String filter) throws MalformedExpressionException {
        List<PageLabel> labels = pageLabelRepository.findByRepositoryIdAndExpressionAndLimitOrderByPage(repositoryId, filter, limit);
        Map<Integer, PageLabel> labelMap = new HashMap<>();
        labels.forEach(l -> labelMap.put(l.getPage(), l));
        PageLabel pageLabel = null;
        for (PageLabel l : labels) {
            if (l.getPage() <= page) {
                pageLabel = l;
            } else {
                break;
            }
        }

        Stream<Occurrence> occurrences = occurrenceRepository.findByRepositoryIdAndAntipatternAndIdGreaterThanEqualOrderById(
                repositoryId,
                Antipattern.DEAD_CODE,
                pageLabel != null ? pageLabel.getOccurrenceId() : 0);

        final List<Occurrence> result = new ArrayList<>();

        log.info("Apply filter = " + filter);
        StopWatch timer = new StopWatch();
        timer.start();

        JexlExpression expr;
        try {
            expr = JEXL_ENGINE.createExpression(filter);
        } catch (JexlException e) {
            throw new MalformedExpressionException(e);
        }
        JexlContext jc = new MapContext();

        final int[] variables = new int[3];
        // variables[0] - how many to skip
        // variables[1] - overall counter
        // variables[2] - page counter
        variables[0] = limit * (page - (pageLabel != null ? pageLabel.getPage() : 0));

        occurrences.peek(o -> {
            jc.set("name", o.getName());
            jc.set("type", o.getType());
            jc.set("kind", o.getKind());
            jc.set("file", o.getFile());
            Object b = expr.evaluate(jc);
            if (b instanceof Boolean) {
                if ((Boolean) b) {
                    variables[1]++;
                    if (variables[1] > variables[0]) {
                        result.add(o);
                    } else {
                        variables[2]++;
                        if (variables[2] > limit) {
                            Integer p = variables[1] / limit;
                            if (labelMap.get(p) == null) {
                                labels.add(new PageLabel(repositoryId, filter, limit, variables[1] / limit, o.getId()));
                            }
                            variables[2] = 0;
                        }
                    }
                }
            } else {
                log.error("Not a boolean result = " + b);
            }
        }).anyMatch(o -> result.size() > limit);

        timer.stop();
        log.info("Apply filter execution time = " + timer.getTotalTimeMillis() + " ms.");

        if (labelMap.get(page) == null) {
            labels.add(new PageLabel(repositoryId, filter, limit, page, result.get(0).getId()));
        }

        boolean exceeds = result.size() > limit;
        if (exceeds) {
            Occurrence o = result.remove(result.size() - 1);
            if (labelMap.get(page + 1) == null) {
                labels.add(new PageLabel(repositoryId, filter, limit, page + 1, o.getId()));
            }
        } else {
            labels.forEach(l -> {
                l.setTotalPages(labels.size());
                l.setTotalElements((labels.size() - 1) * limit + result.size());
            });
        }

        pageLabelRepository.save(labels);

        return new PageImpl<Occurrence>(result) {
            @Override
            public boolean hasNext() {
                return result.size() == limit;
            }

            @Override
            public boolean hasPrevious() {
                return page > 0;
            }

            @Override
            public int getTotalPages() {
                return labels.get(0).getTotalPages();
            }

            @Override
            public long getTotalElements() {
                return labels.get(0).getTotalElements();
            }

            @Override
            public int getNumberOfElements() {
                return result.size();
            }
        };
    }

    @Override
    @Transactional
    public void deleteByRepositoryId(Long repositoryId) {
        pageLabelRepository.deleteByRepositoryId(repositoryId);
        occurrenceRepository.deleteByRepositoryId(repositoryId);
    }

    @Override
    @Transactional
    public void saveBatch(List<Occurrence> occurrences) {
        occurrenceRepository.save(occurrences);
    }

}
