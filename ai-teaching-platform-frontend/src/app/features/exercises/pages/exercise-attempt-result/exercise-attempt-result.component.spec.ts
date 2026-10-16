import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { ExerciseAttemptResultComponent } from './exercise-attempt-result.component';

describe('ExerciseAttemptResultComponent', () => {
  let component: ExerciseAttemptResultComponent;
  let fixture: ComponentFixture<ExerciseAttemptResultComponent>;
  const originalHistoryState = history.state;

  beforeEach(async () => {
    history.replaceState({
      exerciseAttemptResult: {
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
      }
    }, '', window.location.href);

    await TestBed.configureTestingModule({
      imports: [ExerciseAttemptResultComponent],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { id: 'attempt-1' } } }
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseAttemptResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    history.replaceState(originalHistoryState, '', window.location.href);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
