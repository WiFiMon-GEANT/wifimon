package net.geant.wifimon.agent.service;

import net.geant.wifimon.agent.model.VisualOptionsUpdateFormModel;
import net.geant.wifimon.agent.repository.VisualOptionsRepository;
import net.geant.wifimon.model.entity.VisualOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by kokkinos on 27/6/2017.
 */

@Service
public class VisualOptionsServiceImpl implements VisualOptionsService{

    private final VisualOptionsRepository visualOptionsRepository;

    @Autowired
    public VisualOptionsServiceImpl (final VisualOptionsRepository visualOptionsRepository) {
        this.visualOptionsRepository = visualOptionsRepository;
    }

    @Override
    public Optional<VisualOptions> getOptionsByOptionsid(long optionsid) {
        return Optional.ofNullable(visualOptionsRepository.findOne(optionsid));
    }

    @Override
    public Collection<VisualOptions> getAllVisualOptions() {
        return visualOptionsRepository.findAll();
    }

    @Override
    public Collection<VisualOptions> getLastVisualOption() {
        return visualOptionsRepository.getLast();
    }

    @Override
    public VisualOptions create(VisualOptionsUpdateFormModel form) {
        VisualOptions visualOptions = new VisualOptions();
        visualOptions.setRadiuslife(form.getRadiuslife());
        visualOptions.setUnits(form.getUnits());
        visualOptions.setUserdata(form.getUserdata());
        visualOptions.setGrafanasupport(form.getGrafanasupport());
        visualOptions.setElasticsearchsupport(form.getElasticsearchsupport());
        visualOptions.setCorrelationmethod(form.getCorrelationmethod());
        return visualOptionsRepository.save(visualOptions);
    }
}
