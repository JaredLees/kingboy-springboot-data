package com.kingboy.service;

import com.kingboy.repository.user.UserRepository;
import com.kingboy.repository.user.entity.User;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author kingboy--KingBoyWorld@163.com
 * @date 2017/11/28 下午12:53
 * @desc 用户服务.
 */
@Service
public class UserService {

    @Resource
    UserRepository userRepository;

    public Page<User> getUsers() {
        //创建builder
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        //builder下有must、should以及mustNot 相当于sql中的and、or以及not
        //设置模糊搜索,真实姓名中包含金的用户
        builder.must(QueryBuilders.fuzzyQuery("realname", "金"));
        //设置用户名为king
        builder.must(new QueryStringQueryBuilder("king").field("username"));

        //排序
        FieldSortBuilder sort = SortBuilders.fieldSort("age").order(SortOrder.DESC);

        //设置分页
        //====注意!es的分页和Hibernate一样api是从第0页开始的=========
        PageRequest page = new PageRequest(0, 2);

        //构建查询
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //将搜索条件设置到构建中
        nativeSearchQueryBuilder.withQuery(builder);
        //将分页设置到构建中
        nativeSearchQueryBuilder.withPageable(page);
        //将排序设置到构建中
        nativeSearchQueryBuilder.withSort(sort);
        //生产NativeSearchQuery
        NativeSearchQuery query = nativeSearchQueryBuilder.build();

        //执行,返回包装结果的分页
        Page<User> resutlList = userRepository.search(query);

        return resutlList;
    }


}
