package com.mohamedgara.ai_teaching_platform.exercises.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedgara.ai_teaching_platform.exercises.domain.ExerciseContent;
import com.mohamedgara.ai_teaching_platform.exercises.domain.FillInBlankContent;
import com.mohamedgara.ai_teaching_platform.exercises.domain.MultipleChoiceContent;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.SubmitExerciseAttemptRequest;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.attempt.Attempt;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.attempt.FillInBlankAttempt;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.attempt.FillInBlankSentenceAttempt;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.attempt.MultipleChoiceAttempt;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.StartExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.SubmitExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.comparedanswer.ComparedAnswer;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.comparedanswer.FillInBlankComparedAnswer;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.comparedanswer.FillInBlankSentenceComparedAnswer;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.comparedanswer.MultipleChoiceComparedAnswer;
import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseAttempt;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseType;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.ExerciseAttemptNotFoundException;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.ExerciseNotFoundException;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.InvalidAttemptTypeException;
import com.mohamedgara.ai_teaching_platform.exercises.mappers.ExerciseContentMapper;
import com.mohamedgara.ai_teaching_platform.exercises.mappers.ExerciseResponseMapper;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseAttemptRepository;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExerciseAttemptService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseResponseMapper exerciseResponseMapper;
    private final ExerciseAttemptRepository exerciseAttemptRepository;
    private final ObjectMapper objectMapper;
    private final ExerciseContentMapper exerciseContentMapper;

    public StartExerciseResponse startExerciseAttempt(UUID exerciseId){
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow(()-> new ExerciseNotFoundException());

        ExerciseAttempt exerciseAttempt = ExerciseAttempt
                .builder()
                .exercise(exercise)
                .build();

        ExerciseAttempt savedExerciseAttempt = exerciseAttemptRepository.save(exerciseAttempt);

        return exerciseResponseMapper.toStartExerciseResponse(exercise,savedExerciseAttempt.getId());
    }

    public SubmitExerciseResponse submitExerciseAttempt(UUID exerciseAttemptId, SubmitExerciseAttemptRequest submitExerciseAttemptRequest) {
        ExerciseAttempt exerciseAttempt = exerciseAttemptRepository.findById(exerciseAttemptId)
                .orElseThrow(()-> new ExerciseAttemptNotFoundException());

        Exercise exercise = exerciseAttempt.getExercise();

        long timeTaken = Duration.between(
                exerciseAttempt.getCreatedAt(),
                LocalDateTime.now()
        ).toSeconds();

        ExerciseType exerciseType = exercise.getType();

        if(!submitExerciseAttemptRequest.exerciseType().equals(exercise.getType())) throw new InvalidAttemptTypeException();

        ExerciseContent exerciseContent = exerciseContentMapper.toExerciseContent(
                exercise.getContent(), exerciseType
        );

        String aiFeedBack = "";

        ScoredComparedAnswer scoredComparedAnswer = switch (exerciseType){
            case MULTIPLE_CHOICE -> compareAnswerMultipleChoice(submitExerciseAttemptRequest.attempt(), exerciseContent);
            case FILL_IN_BLANK -> compareAnswerFillInBlank(submitExerciseAttemptRequest.attempt(), exerciseContent);
        };

        return exerciseResponseMapper.toSubmitExerciseResponse(
                exercise,
                exerciseAttempt.getId(),
                scoredComparedAnswer.score,
                aiFeedBack,
                timeTaken,
                scoredComparedAnswer.comparedAnswer
        );

    }

    private record ScoredComparedAnswer(int score, ComparedAnswer comparedAnswer){};

    private ScoredComparedAnswer compareAnswerMultipleChoice(Attempt attempt, ExerciseContent exerciseContent ){
        if (!(attempt instanceof MultipleChoiceAttempt multipleChoiceAttempt)) {
            throw new InvalidAttemptTypeException();
        }
        MultipleChoiceContent exerciseMultipleChoiceContent = (MultipleChoiceContent) exerciseContent;

        boolean isCorrect = exerciseMultipleChoiceContent.correctAnswer()
                .equalsIgnoreCase(multipleChoiceAttempt.answer());

         int score = isCorrect?10:0;

         MultipleChoiceComparedAnswer multipleChoiceComparedAnswer = new MultipleChoiceComparedAnswer(
                 exerciseMultipleChoiceContent.question(),
                 exerciseMultipleChoiceContent.options(),
                 exerciseMultipleChoiceContent.correctAnswer(),
                 multipleChoiceAttempt.answer(),
                 isCorrect
         );

        return  new ScoredComparedAnswer(
                score,
                multipleChoiceComparedAnswer
        );
    };

    private ScoredComparedAnswer compareAnswerFillInBlank(Attempt attempt, ExerciseContent exerciseContent){
        if (!(attempt instanceof FillInBlankAttempt fillInBlankAttempt)) {
            throw new InvalidAttemptTypeException();
        }
        FillInBlankContent exerciseFillInBlankContent = (FillInBlankContent) exerciseContent;

        List<FillInBlankSentenceComparedAnswer> sentences = exerciseFillInBlankContent.sentences()
                .stream().map(sentence->{
                    FillInBlankSentenceAttempt attemptSentence = fillInBlankAttempt.sentences().stream()
                            .filter(sentenceAttempt -> sentenceAttempt.sentenceId().equals(sentence.id()))
                            .findFirst().orElseThrow(()-> new InvalidAttemptTypeException());

                    boolean isAnswerCorrect = sentence.answers().stream().anyMatch(
                            option -> option.equalsIgnoreCase(attemptSentence.answer())
                    );

                    return new FillInBlankSentenceComparedAnswer(
                            sentence.text(),
                            sentence.answers(),
                            attemptSentence.answer(),
                            isAnswerCorrect
                    );
                }).toList();

        int correctAnswers = (int) sentences.stream().filter(sentence->sentence.isCorrect()).count();
        int score = (int) Math.round((double) correctAnswers / sentences.size() * 10);

        return new ScoredComparedAnswer(
                score,
                new FillInBlankComparedAnswer(sentences)
        );
    }
}
