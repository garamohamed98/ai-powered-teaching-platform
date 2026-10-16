import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FillInBlankAttemptResultComponent } from './fill-in-blank-attempt-result.component';

describe('FillInBlankAttemptResultComponent', () => {
  let component: FillInBlankAttemptResultComponent;
  let fixture: ComponentFixture<FillInBlankAttemptResultComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FillInBlankAttemptResultComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FillInBlankAttemptResultComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('attemptResult', {
      attemptId: 'attempt-1',
      exerciseId: 'exercise-1',
      lessonIdList: [],
      type: 'FILL_IN_BLANK',
      title: 'Java basics',
      instructions: 'Complete the sentence.',
      comparedAnswer: {
        sentences: [
          {
            text: 'Java is a programming language.',
            answers: ['Java'],
            submittedAnswer: 'Java',
            isCorrect: true
          }
        ]
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
