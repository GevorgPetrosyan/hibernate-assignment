package com.egs.hibernate.service.impl;

import com.arakelian.faker.model.Person;
import com.arakelian.faker.service.RandomAddress;
import com.arakelian.faker.service.RandomPerson;
import com.egs.hibernate.dto.response.UserCountByCountryCode;
import com.egs.hibernate.dto.response.UserResponse;
import com.egs.hibernate.entity.Address;
import com.egs.hibernate.entity.Country;
import com.egs.hibernate.entity.PhoneNumber;
import com.egs.hibernate.entity.User;
import com.egs.hibernate.exception.domain.PaginationPageException;
import com.egs.hibernate.exception.domain.PaginationSizeException;
import com.egs.hibernate.exception.domain.PaginationSortException;
import com.egs.hibernate.mapper.UserMapper;
import com.egs.hibernate.repository.CountryRepository;
import com.egs.hibernate.repository.UserRepository;
import com.egs.hibernate.service.UserService;
import com.neovisionaries.i18n.CountryCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateUsers(final int count) {
        int i = userRepository.findFirstByOrderByIdDesc()
                .map(User::getUsername)
                .map(it -> it.split("_")[1])
                .map(Integer::valueOf)
                .map(it -> ++it)
                .orElse(0);
        final int terminate = i + count;
        List<User> users = new ArrayList<>();
        Map<Long, Country> countryMap = new HashMap<>();
        List<Country> countries = countryRepository.findAll();
        countries.forEach(country -> countryMap.put(country.getId(), country));
        for (; i < terminate; i++) {
            final String username = "username_" + i;
            try {
                final User user = constructUser(username);
                final Set<Address> addresses = constructAddresses(user, countryMap.get(ThreadLocalRandom.current().nextLong(1L, 272L)));
                final PhoneNumber phoneNumber = constructPhoneNumber(user);
                user.setPhoneNumbers(Set.of(phoneNumber));
                user.setAddresses(addresses);
                users.add(user);
            } catch (final Exception e) {
                log.warn("User with username: {} can't be created. {}", username, e.getMessage());
            }
        }
        userRepository.saveAll(users);
    }

    @Override
    @Transactional
    public List<UserResponse> findAllUsers(int page, int size, String field) throws PaginationSizeException {
        checkPaginationFieldsAndSort(page, size, field);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(field));
        return userRepository.findAllUsers(pageRequest)
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserCountByCountryCode findAllUsersByCountryId(String code) {
        Query query = entityManager.createNativeQuery("select distinct count(u.\"id\") from users u" +
                " left join address a on u.id = a.user_id and" +
                " a.country_id = (select id from country c where c.country_code =:code) " +
                "where a.country_id is not null;");
        query.setParameter("code", code);
        BigInteger result = (BigInteger) query.getSingleResult();

        return new UserCountByCountryCode(code, result.intValue());
    }

    @Override
    public List<CountryCode> findAllCountriesByUserCount() {
        Query query = entityManager.createNativeQuery("select country.country_code from users " +
                "left join address  on users.id = address.user_id " +
                "left join country  on country.id =address.country_id " +
                "where address.country_id is not null group by country_code " +
                "having count(users.id) > 10000");
        return (List<CountryCode>) query.getResultList();
    }

    private static PhoneNumber constructPhoneNumber(User user) {
        return PhoneNumber.builder().phoneNumber(String.valueOf(ThreadLocalRandom.current().nextLong(100000000L, 999999999L)))
                .user(user).build();
    }

    private Set<Address> constructAddresses(User user, Country country) {
        return RandomAddress.get().listOf(2).stream()
                .map(fakeAddress -> Address.builder().city(fakeAddress.getCity()).postalCode(fakeAddress.getPostalCode())
                        .country(country)
                        .user(user).build()).collect(Collectors.toSet());
    }

    private static User constructUser(String username) {
        final Person person = RandomPerson.get().next();
        return User.builder().firstName(person.getFirstName())
                .lastName(person.getLastName()).username(username)
                .birthdate(person.getBirthdate().toLocalDate()).build();
    }

    void checkPaginationFieldsAndSort(int page, int size, String field) {
        if (size <= 0) {
            throw new PaginationSizeException("size should be greater than 0");
        }
        if (page < 0) {
            throw new PaginationPageException("page should be equals or greater than 0");
        }
        if (field == null) {
            throw new PaginationSortException("please choose one correct field for sorting");
        }
    }

}
