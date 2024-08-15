package com.yunshu.aianswer;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yunshu.aianswer.model.entity.UserAnswer;
import com.yunshu.aianswer.service.UserAnswerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class ShardingTest {

    @Resource
    private UserAnswerService userAnswerService;

    @Test
    void test() {

        List<UserAnswer> list = userAnswerService.list(Wrappers.lambdaQuery(UserAnswer.class).eq(UserAnswer::getAppType, 0));
        for (UserAnswer userAnswer : list) {
            System.out.println(userAnswer.toString());
        }
    }
}
