package com.wyy.esdemo.util;

import org.elasticsearch.index.query.*;

/**
 * @Date: 19-10-21
 * @Author: wyy
 */
public class ESQueryBuilder {


    public static QueryBuilder and(QueryBuilder... queryBuilders) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (QueryBuilder queryBuilder : queryBuilders) {
            boolQueryBuilder.must(queryBuilder);
        }
        return boolQueryBuilder;
    }

    public static QueryBuilder or(QueryBuilder... queryBuilders) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (QueryBuilder queryBuilder : queryBuilders) {
            boolQueryBuilder.should(queryBuilder);
        }
        return boolQueryBuilder;
    }

    public static QueryBuilder eq(String key, Object value) {
        return QueryBuilders.matchQuery(key, value);
    }

    public static QueryBuilder ne(String key, Object value) {
        return new BoolQueryBuilder().mustNot(QueryBuilders.matchQuery(key, value));
    }

    public static QueryBuilder gt(String key, Object value) {
        return QueryBuilders.rangeQuery(key).gt(value);
    }

    public static QueryBuilder lt(String key, Object value) {
        return QueryBuilders.rangeQuery(key).lt(value);
    }

    public static QueryBuilder gte(String key, Object value) {
        return QueryBuilders.rangeQuery(key).gte(value);
    }

    public static QueryBuilder lte(String key, Object value) {
        return QueryBuilders.rangeQuery(key).lte(value);
    }

    public static QueryBuilder in(String key, String[] values) {
        return new TermsQueryBuilder(key, values);
    }

    public static QueryBuilder in(String key, int[] values) {
        return new TermsQueryBuilder(key, values);
    }

    public static QueryBuilder in(String key, long[] values) {
        return new TermsQueryBuilder(key, values);
    }

    public static QueryBuilder in(String key, float[] values) {
        return new TermsQueryBuilder(key, values);
    }

    public static QueryBuilder in(String key, double[] values) {
        return new TermsQueryBuilder(key, values);
    }

    public static QueryBuilder in(String key, Object[] values) {
        return new TermsQueryBuilder(key, values);
    }

    public static QueryBuilder in(String key, Iterable<?> values) {
        return new TermsQueryBuilder(key, values);
    }

    public static QueryBuilder between(String key, Object from, Object to) {
        return new RangeQueryBuilder(key).gte(from).lte(to);
    }

    public static QueryBuilder like(String key, String value) {
        return QueryBuilders.wildcardQuery(key, value);
    }
}
