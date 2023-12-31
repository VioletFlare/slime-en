package com.github.nekolr.slime.support;

import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * Pagination Request
 */
@Getter
public class PageRequest {

    /**
     * Page number，First page will be 1
     */
    private int page;

    /**
     * Number of records per page
     */
    private int size;

    /**
     * Sort List
     */
    private List<Sort.Order> sorts = new ArrayList<>();


    public PageRequest() {
        this.page = 1;
        this.size = 10;
    }

    public PageRequest(int page, int size) {
        // When passing a page number less or equal 0 时，Default to first page
        if (page <= 0) {
            page = 1;
        }

        // When passing each page number to 1 时，Default to 1
        if (size < 1) {
            size = 1;
        }

        this.page = page;
        this.size = size;
    }

    public void setPage(int page) {
        if (page <= 0) {
            page = 1;
        }
        this.page = page;
    }

    public void setSize(int size) {
        if (size < 1) {
            size = 1;
        }
        this.size = size;
    }

    /**
     * Add a sorting
     *
     * @param property Sort Text
     */
    public void addAscOrder(String property) {
        sorts.add(new Sort.Order(Sort.Direction.ASC, property));
    }

    /**
     * Add a sorting
     *
     * @param property Sort Text
     */
    public void addDescOrder(String property) {
        sorts.add(new Sort.Order(Sort.Direction.DESC, property));
    }


    /**
     * Generating Pageable
     *
     * @return
     */
    public Pageable toPageable() {
        return org.springframework.data.domain.PageRequest.of(this.page - 1, this.size, Sort.by(sorts));
    }
}
