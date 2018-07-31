package com.sitexa.eoscmd.data.local.repository;

import java.util.List;

public interface EosAccountRepository {
    void addAll(String... accountNames);

    void addAll(List<String> accountNames);

    void addAccount(String accountName);

    void deleteAll();

    void delete(String accountName);

    /**
     * get account list
     *
     * @return
     */
    List<String> getAll();

    List<String> searchName(String nameStarts);
}
