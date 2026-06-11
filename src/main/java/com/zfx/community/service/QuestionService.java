package com.zfx.community.service;

import com.zfx.community.dto.PaginationDTO;
import com.zfx.community.dto.QuestionDTO;
import com.zfx.community.dto.QuestionQueryDTO;
import com.zfx.community.enums.SortEnum;
import com.zfx.community.exception.CustomizeErrorCode;
import com.zfx.community.exception.CustomizeException;
import com.zfx.community.mapper.QuestionExtMapper;
import com.zfx.community.mapper.QuestionMapper;
import com.zfx.community.mapper.UserMapper;
import com.zfx.community.model.Question;
import com.zfx.community.model.QuestionExample;
import com.zfx.community.model.User;
import com.zfx.community.model.UserExample;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 问题服务
 */
@Service
public class QuestionService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private QuestionMapper questionMapper;

    private static int calculateTotalPage(int totalCount, int size) {
        return (totalCount + size - 1) / size;
    }

    private static int normalizePage(int page, int totalPage) {
        if (page < 1) return 1;
        if (page > totalPage) return totalPage;
        return page;
    }

    /**
     * 批量加载用户，避免 N+1 查询
     */
    private Map<Long, User> batchLoadUsers(List<Long> creatorIds) {
        if (creatorIds.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        UserExample example = new UserExample();
        example.createCriteria().andIdIn(creatorIds);
        List<User> users = userMapper.selectByExample(example);
        return users.stream().collect(Collectors.toMap(User::getId, u -> u, (a, b) -> a));
    }

    /**
     * 将 Question 列表转为 QuestionDTO 列表，批量填充用户信息
     */
    private List<QuestionDTO> buildQuestionDTOList(List<Question> questions) {
        List<Long> creatorIds = questions.stream()
                .map(Question::getCreator)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> userMap = batchLoadUsers(creatorIds);

        return questions.stream().map(question -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(userMap.get(question.getCreator()));
            return questionDTO;
        }).collect(Collectors.toList());
    }

    public PaginationDTO list(String search, String tag, String sort, Integer page, Integer size) {
        if (StringUtils.isNotBlank(search)) {
            StringUtils.split(search, " ");
        }

        PaginationDTO paginationDTO = new PaginationDTO();
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        if (StringUtils.isNotBlank(tag)) {
            tag = tag.replace("+", "").replace("*", "").replace("?", "");
            questionQueryDTO.setTag(tag);
        }

        for (SortEnum sortEnum : SortEnum.values()) {
            if (sortEnum.name().toLowerCase().equals(sort)) {
                questionQueryDTO.setSort(sort);

                if (sortEnum == SortEnum.HOT7) {
                    questionQueryDTO.setTime(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 7);
                }
                if (sortEnum == SortEnum.HOT30) {
                    questionQueryDTO.setTime(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30);
                }
                break;
            }
        }

        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);
        Integer totalPage = calculateTotalPage(totalCount, size);
        page = normalizePage(page, totalPage);

        paginationDTO.setPagination(totalPage, page);
        Integer offset = page < 1 ? 0 : size * (page - 1);
        questionQueryDTO.setSize(size);
        questionQueryDTO.setPage(offset);
        List<Question> questions = questionExtMapper.selectBySearch(questionQueryDTO);

        paginationDTO.setData(buildQuestionDTOList(questions));
        return paginationDTO;
    }

    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();

        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(questionExample);
        Integer totalPage = calculateTotalPage(totalCount, size);
        page = normalizePage(page, totalPage);

        paginationDTO.setPagination(totalPage, page);

        Integer offset = Math.max(0, (page - 1) * size);
        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        example.setOrderByClause("GMT_CREATE DESC");
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));

        paginationDTO.setData(buildQuestionDTOList(questions));
        return paginationDTO;
    }

    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if (StringUtils.isBlank(queryDTO.getTag())) {
            return new ArrayList<>();
        }
        String[] tags = StringUtils.split(queryDTO.getTag(), ",");
        String regexpTag = Arrays
                .stream(tags)
                .filter(StringUtils::isNotBlank)
                .map(t -> t.replace("+", "").replace("*", "").replace("?", ""))
                .collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);

        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }

    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setCommentCount(0);
            question.setLikeCount(0);
            questionMapper.insert(question);
        } else {
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria().andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion, example);
            if (updated != 1) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void delete(Long id, Long userId) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        if (!question.getCreator().equals(userId)) {
            throw new CustomizeException(CustomizeErrorCode.INVALID_OPERATION);
        }
        questionMapper.deleteByPrimaryKey(id);
    }
}
