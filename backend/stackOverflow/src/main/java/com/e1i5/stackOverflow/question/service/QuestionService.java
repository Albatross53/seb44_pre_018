package com.e1i5.stackOverflow.question.service;

import com.e1i5.stackOverflow.comment.service.CommentService;
import com.e1i5.stackOverflow.exception.BusinessLogicException;
import com.e1i5.stackOverflow.exception.ExceptionCode;
import com.e1i5.stackOverflow.member.entity.Member;
import com.e1i5.stackOverflow.member.service.MemberService;
import com.e1i5.stackOverflow.question.entity.Question;
import com.e1i5.stackOverflow.question.repository.QuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final CommentService commentService;
    private final MemberService memberService;

    public QuestionService(QuestionRepository questionRepository,
                                 CommentService commentService ,
                                           MemberService memberService
    ) {
        this.questionRepository = questionRepository;
         this.memberService = memberService;
         this.commentService = commentService;

    }

    public Question createQuestion(Question question) { //질문 생성 (회원만 질문작성가능)
        verifyExistsTitle(question.getTitle());
        memberService.findVerifiedMemberById(question.getMember().getMemberId()); //회원인지 확인
            return questionRepository.save(question);

    }




    public Question updateQuestion(Question question) { //질문 수정 (질문 수정은 작성자만 가능)
        Question findQuestion = findVerifiedQuestion(question.getQuestionId());//요청된 질문이 DB에 없으면 에러

        Optional.ofNullable(question.getTitle()) //제목수정
                .ifPresent(questionTitle->findQuestion.setTitle(questionTitle));
        Optional.ofNullable(question.getContent()) //내용수정
                .ifPresent(questionContent->findQuestion.setContent(questionContent));
        Optional.ofNullable(question.getQuestionStatus()) //글 삭제
                .ifPresent(questionStatus->findQuestion.setQuestionStatus(questionStatus));

        Question updatedQuestion = questionRepository.save(findQuestion);

        return updatedQuestion;

    }



    public Question findQuestion(long questionId) { //질문 검색
        Question findQuestion = findVerifiedQuestion(questionId); //요청된 질문이 DB에 없으면 에러

        findQuestion.setView(findQuestion.getView()+1); //view 1증가
        questionRepository.save(findQuestion); // 수정후 DB에 저장

        return findQuestion;
    }
    public Page<Question> findQuestions(int page, int size) { //여러 질문 검색
        return questionRepository.findAll(PageRequest.of(page, size,
                Sort.by("questionId").descending()));
    }

    public void deleteQuestion(long questionId) {
        Question findQuestion = findQuestion(questionId);
        questionRepository.delete(findQuestion);
    }


    private void verifyExistsTitle(String title) {//이미 글이 존재하면 에러
        Optional<Question> question = questionRepository.findByTitle(title);
        if (question.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_EXISTS);
        }
    }
    public Question findVerifiedQuestion(long questionId) { //요청된 질문이 DB에 없으면 에러
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        Question findQuestion = optionalQuestion.orElseThrow(() -> new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
        return findQuestion;

    }


    //질문글 수정,삭제시 질문 작성자만 가능하게하도록하는 메서드
    public void QuestionByAuthor(long questionId, long memberId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));

        if (question.getMember().getMemberId() != memberId) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_MEMBER_NOT_MATCH);
        }
    }
}


