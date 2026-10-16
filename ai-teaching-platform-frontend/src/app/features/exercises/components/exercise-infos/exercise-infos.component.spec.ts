import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseInfosComponent } from './exercise-infos.component';

describe('ExerciseInfosComponent', () => {
  let component: ExerciseInfosComponent;
  let fixture: ComponentFixture<ExerciseInfosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseInfosComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseInfosComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('exercise', {
      id: 'exercise-1',
      title: 'Java basics',
      type: 'MULTIPLE_CHOICE',
      lessonList: [],
      instructions: 'Choose the correct answer.',
      content: {
        question: 'What is Java?',
        options: ['A language', 'A database'],
        correctAnswer: 'A language'
      }
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
