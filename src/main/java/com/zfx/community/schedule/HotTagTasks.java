package com.zfx.community.schedule;

import com.zfx.community.cache.HotTagCache;
import com.zfx.community.mapper.QuestionMapper;
import com.zfx.community.model.Question;
import com.zfx.community.model.QuestionExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class HotTagTasks {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private HotTagCache hotTagCache;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
    public void hotTagSchedule() {
        int offset = 0;
        int limit = 20;
        log.info("hotTagSchedule start");

        List<Question> list;
        Map<String, Integer> priorities = new HashMap<>();

        do {
            list = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, limit));
            for (Question question : list) {
                String[] tags = StringUtils.split(question.getTag(), ",");
                if (tags == null) continue;
                for (String tag : tags) {
                    Integer priority = priorities.getOrDefault(tag, 0);
                    priorities.put(tag, priority + 5 + question.getCommentCount());
                }
            }
            offset += limit;
        } while (list.size() == limit);

        hotTagCache.updateTags(priorities);
        log.info("hotTagSchedule stop, updated {} tags", priorities.size());
    }
}
