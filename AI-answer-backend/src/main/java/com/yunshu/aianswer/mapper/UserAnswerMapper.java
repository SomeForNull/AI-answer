package com.yunshu.aianswer.mapper;

import com.yunshu.aianswer.model.dto.statistic.AppAnswerCountDTO;
import com.yunshu.aianswer.model.dto.statistic.AppAnswerResultCountDTO;
import com.yunshu.aianswer.model.entity.UserAnswer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 云舒
 * @description 针对表【user_answer(用户答题记录)】的数据库操作Mapper
 * @createDate 2024-05-09 20:41:03
 * @Entity com.yunshu.yudada.model.entity.UserAnswer
 */
public interface UserAnswerMapper extends BaseMapper<UserAnswer> {
    @Select("select appId, count(userId) as answerCount from user_answer " +
            "group by appId order by answerCount desc")
    List<AppAnswerCountDTO> doAppAnswerCount();

    @Select("select resultName, count(resultName) as resultCount from user_answer " +
            "where appId = #{appId} group by resultName order by resultCount desc")
    List<AppAnswerResultCountDTO> doAppAnswerResultCount(Long appId);

}





