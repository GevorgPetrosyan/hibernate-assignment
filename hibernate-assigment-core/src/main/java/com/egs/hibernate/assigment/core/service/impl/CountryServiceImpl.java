package com.egs.hibernate.assigment.core.service.impl;

import com.egs.hibernate.assigment.core.entity.Country;
import com.egs.hibernate.assigment.core.repository.CountryRepository;
import com.egs.hibernate.assigment.core.service.CountryService;
import com.egs.hibernate.assigment.core.service.UserService;
import com.egs.hibernate.assigment.data.transfer.response.CountryCodesAndCountOfUsersResponse;
import com.neovisionaries.i18n.CountryCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final UserService userService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void storeAllCountries() {
        if (countryRepository.count() == 0) {
            Arrays.stream(CountryCode.values())
                    .map(it -> Country.builder().countryCode(it).displayName(it.getName()).build())
                    .forEach(countryRepository::save);
        }
    }

    @Override
    public List<String> getAllByUsersCount(Long validateCount) {
        List<CountryCodesAndCountOfUsersResponse> usersCountByCountryCode =
                userService.getUsersCountByCountryCode(validateCount);
        return usersCountByCountryCode.stream()
                .map(CountryCodesAndCountOfUsersResponse::getCountryCode)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
