import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MultipleChoiceAttemptResultComponent } from './multiple-choice-attempt-result.component';

describe('MultipleChoiceAttemptResultComponent', () => {
  let component: MultipleChoiceAttemptResultComponent;
  let fixture: ComponentFixture<MultipleChoiceAttemptResultComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MultipleChoiceAttemptResultComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MultipleChoiceAttemptResultComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('attemptResult', {
      attemptId: 'attempt-1',
      exerciseId: 'exercise-1',
      lessonIdList: [],
      type: 'MULTIPLE_CHOICE',
      title: 'Java basics',
      instructions: 'Choose the correct answer.',
      comparedAnswer: {
        question: 'What is Java?',
        options: ['A language', 'A database'],
        correctAnswers: 'A language',
        submittedAnswer: 'A language',
        isCorrect: true
      },
      score: 1,
      aiFeedback: 'Correct.',
      timeTaken: 1000
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
