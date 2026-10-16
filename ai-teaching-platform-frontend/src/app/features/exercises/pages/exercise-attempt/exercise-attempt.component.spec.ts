import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { MessageService } from 'primeng/api';

import { ExerciseAttemptComponent } from './exercise-attempt.component';

describe('ExerciseAttemptComponent', () => {
  let component: ExerciseAttemptComponent;
  let fixture: ComponentFixture<ExerciseAttemptComponent>;
  const originalHistoryState = history.state;

  beforeEach(async () => {
    history.replaceState({
      attempt: {
        id: 'exercise-1',
        exerciseAttemptId: 'attempt-1',
        title: 'Java basics',
        type: 'MULTIPLE_CHOICE',
        instructions: 'Choose the correct answer.',
        content: {
          question: 'What is Java?',
          options: ['A language', 'A database'],
          correctAnswer: 'A language'
        }
      }
    }, '', window.location.href);

    await TestBed.configureTestingModule({
      imports: [ExerciseAttemptComponent],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        MessageService,
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { id: 'attempt-1' } } }
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseAttemptComponent);
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
