import { Component, input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { FormGroup } from '@angular/forms';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { CreateExerciseFormModalComponent } from './create-exercise-form-modal.component';
import { ExercisesService } from '../../services/exercises.service';
import { Exercise } from '../../models/exercise.model';
import { Lesson } from '../../models/lesson.model';
import { MultipleChoiceFormComponent } from '../../forms/multiple-choice-form/multiple-choice-form.component';
import { FillInBlankFormComponent } from '../../forms/fill-in-blank-form/fill-in-blank-form.component';

@Component({ selector: 'app-multiple-choice-form', template: '' })
class StubMultipleChoiceFormComponent {
  form = input.required<FormGroup>();
}

@Component({ selector: 'app-fill-in-blank-form', template: '' })
class StubFillInBlankFormComponent {
  form = input.required<FormGroup>();
  createSentence = input.required<() => FormGroup>();
}

describe('CreateExerciseFormModalComponent', () => {
  let component: CreateExerciseFormModalComponent;
  let fixture: ComponentFixture<CreateExerciseFormModalComponent>;
  let exercisesService: jasmine.SpyObj<ExercisesService>;
  let messageService: jasmine.SpyObj<MessageService>;

  const mockLessons: Lesson[] = [
    { id: 'lesson-1', title: 'Introduction' },
    { id: 'lesson-2', title: 'Advanced topics' },
  ];

  const mockExercise: Exercise = {
    id: 'exercise-1',
    title: 'Introduction quiz',
    type: 'MULTIPLE_CHOICE',
    lessonList: [mockLessons[0]],
    instructions: 'Choose an answer',
    content: {},
  };

  beforeEach(async () => {
    exercisesService = jasmine.createSpyObj(
      'ExercisesService',
      ['createExercise'],
    );
    messageService = jasmine.createSpyObj(
      'MessageService',
      ['add'],
    );
    exercisesService.createExercise.and.returnValue(of(mockExercise));

    await TestBed.configureTestingModule({
      imports: [CreateExerciseFormModalComponent],
      providers: [
        provideNoopAnimations(),
        { provide: ExercisesService, useValue: exercisesService },
        { provide: MessageService, useValue: messageService },
      ],
    })
      .overrideComponent(CreateExerciseFormModalComponent, {
        remove: {
          imports: [MultipleChoiceFormComponent, FillInBlankFormComponent],
        },
        add: {
          imports: [StubMultipleChoiceFormComponent, StubFillInBlankFormComponent],
        },
      })
      .compileComponents();

    fixture = TestBed.createComponent(CreateExerciseFormModalComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('isVisible', false);
    fixture.componentRef.setInput('lessons', mockLessons);
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  describe('form validation', () => {
    it('should be invalid when required fields are empty', () => {
      fixture.detectChanges();

      expect(component.form.valid).toBeFalse();
    });

    it('should be invalid when the title has less than 3 characters', () => {
      fixture.detectChanges();
      component.form.patchValue({ title: 'Ab' });

      expect(component.form.get('title')?.valid).toBeFalse();
    });

    it('should be invalid when the instructions have less than 3 characters', () => {
      fixture.detectChanges();
      component.form.patchValue({ instructions: 'Ab' });

      expect(component.form.get('instructions')?.valid).toBeFalse();
    });

    it('should be invalid when the exercise type has not been selected', () => {
      fixture.detectChanges();
      component.form.patchValue({
        title: 'Exercise title',
        instructions: 'Exercise instructions',
      });

      expect(component.form.get('exerciseType')?.valid).toBeFalse();
    });

    it('should be valid when required fields and multiple-choice content are valid', () => {
      fixture.detectChanges();
      component.onTypeChange('MULTIPLE_CHOICE');
      component.form.patchValue({
        title: 'Exercise title',
        instructions: 'Exercise instructions',
        exerciseType: { label: 'Multiple Choice', value: 'MULTIPLE_CHOICE' },
        content: {
          question: 'Choose the correct answer',
          options: ['A', 'B'],
          correctAnswer: 'A',
        },
      });

      expect(component.form.valid).toBeTrue();
    });

    it('should create the first blank sentence for fill-in-the-blank content', () => {
      fixture.detectChanges();
      component.onTypeChange('FILL_IN_BLANK');

      const sentences = component.content.get('sentences');

      expect(component.exerciseType()).toBe('FILL_IN_BLANK');
      expect(sentences?.value).toEqual([{ question: '', answers: [] }]);
      expect(sentences?.valid).toBeFalse();
    });

    it('should be valid when required fields and fill-in-the-blank content are valid', () => {
      fixture.detectChanges();
      component.onTypeChange('FILL_IN_BLANK');
      component.form.patchValue({
        title: 'Exercise title',
        instructions: 'Exercise instructions',
        exerciseType: { label: 'Fill in blank', value: 'FILL_IN_BLANK' },
        content: {
          sentences: [{ question: 'The sky is blue', answers: ['sky'] }],
        },
      });

      expect(component.form.valid).toBeTrue();
    });
  });

  describe('onTypeChange', () => {
    it('should set the multiple-choice content controls', () => {
      fixture.detectChanges();

      component.onTypeChange('MULTIPLE_CHOICE');

      expect(component.exerciseType()).toBe('MULTIPLE_CHOICE');
      expect(component.content.controls['question']).toBeTruthy();
      expect(component.content.controls['options']).toBeTruthy();
      expect(component.content.controls['correctAnswer']).toBeTruthy();
    });

    it('should set the fill-in-the-blank sentences control', () => {
      fixture.detectChanges();

      component.onTypeChange('FILL_IN_BLANK');

      expect(component.exerciseType()).toBe('FILL_IN_BLANK');
      expect(component.content.get('sentences')).toBeTruthy();
    });
  });

  describe('submit', () => {
    it('should not call createExercise when the form is invalid', () => {
      fixture.detectChanges();

      component.submit();

      expect(exercisesService.createExercise).not.toHaveBeenCalled();
    });

    it('should call createExercise with the selected lessons and form content', () => {
      fixture.detectChanges();
      component.onTypeChange('MULTIPLE_CHOICE');
      component.form.patchValue({
        title: 'Exercise title',
        instructions: 'Exercise instructions',
        exerciseType: { label: 'Multiple Choice', value: 'MULTIPLE_CHOICE' },
        lessons: mockLessons,
        correctAnswers: true,
        content: {
          question: 'Choose the correct answer',
          options: ['A', 'B'],
          correctAnswer: 'A',
        },
      });

      component.submit();

      expect(exercisesService.createExercise).toHaveBeenCalledWith({
        title: 'Exercise title',
        instructions: 'Exercise instructions',
        type: 'MULTIPLE_CHOICE',
        lessonIdList: ['lesson-1', 'lesson-2'],
        correctAnswers: true,
        content: {
          question: 'Choose the correct answer',
          options: ['A', 'B'],
          correctAnswer: 'A',
        },
      });
    });

    it('should display a success message after creation', () => {
      fixture.detectChanges();
      component.onTypeChange('MULTIPLE_CHOICE');
      component.form.patchValue({
        title: 'Exercise title',
        instructions: 'Exercise instructions',
        exerciseType: { label: 'Multiple Choice', value: 'MULTIPLE_CHOICE' },
        lessons: mockLessons,
        content: {
          question: 'Choose the correct answer',
          options: ['A', 'B'],
          correctAnswer: 'A',
        },
      });

      component.submit();

      expect(messageService.add).toHaveBeenCalledWith(
        jasmine.objectContaining({
          severity: 'success',
          detail: 'Exercise has been created successfully related to lesson Introduction, Advanced topics.',
        }),
      );
    });

    it('should emit exerciseCreated after creation', () => {
      fixture.detectChanges();
      component.onTypeChange('MULTIPLE_CHOICE');
      component.form.patchValue({
        title: 'Exercise title',
        instructions: 'Exercise instructions',
        exerciseType: { label: 'Multiple Choice', value: 'MULTIPLE_CHOICE' },
        content: {
          question: 'Choose the correct answer',
          options: ['A', 'B'],
          correctAnswer: 'A',
        },
      });
      spyOn(component.exerciseCreated, 'emit');

      component.submit();

      expect(component.exerciseCreated.emit).toHaveBeenCalled();
    });

    it('should close the modal after creation', () => {
      fixture.detectChanges();
      component.onTypeChange('MULTIPLE_CHOICE');
      component.form.patchValue({
        title: 'Exercise title',
        instructions: 'Exercise instructions',
        exerciseType: { label: 'Multiple Choice', value: 'MULTIPLE_CHOICE' },
        content: {
          question: 'Choose the correct answer',
          options: ['A', 'B'],
          correctAnswer: 'A',
        },
      });
      spyOn(component.isVisibleChange, 'emit');

      component.submit();

      expect(component.isVisible).toBeFalse();
      expect(component.isVisibleChange.emit).toHaveBeenCalledWith(false);
    });
  });

  describe('close', () => {
    it('should emit isVisibleChange with false when closed', () => {
      fixture.detectChanges();
      spyOn(component.isVisibleChange, 'emit');

      component.close();

      expect(component.isVisible).toBeFalse();
      expect(component.isVisibleChange.emit).toHaveBeenCalledWith(false);
    });
  });

  describe('child component inputs', () => {
    it('should pass the exercise content form to the multiple-choice form', () => {
      fixture.componentRef.setInput('isVisible', true);
      fixture.detectChanges();
      component.onTypeChange('MULTIPLE_CHOICE');
      fixture.detectChanges();
      const child = fixture.debugElement.query(
        By.directive(StubMultipleChoiceFormComponent),
      ).componentInstance as StubMultipleChoiceFormComponent;

      expect(child.form()).toBe(component.content);
    });

    it('should pass the content form and sentence factory to the fill-in-the-blank form', () => {
      fixture.componentRef.setInput('isVisible', true);
      fixture.detectChanges();
      component.onTypeChange('FILL_IN_BLANK');
      fixture.detectChanges();
      const child = fixture.debugElement.query(
        By.directive(StubFillInBlankFormComponent),
      ).componentInstance as StubFillInBlankFormComponent;

      expect(child.form()).toBe(component.content);
      expect(child.createSentence()().value).toEqual({ question: '', answers: [] });
    });

    it('should pass the lessons to the related lessons selector', () => {
      fixture.componentRef.setInput('isVisible', true);
      fixture.detectChanges();
      const lessonsSelect = fixture.debugElement.query(By.css('p-multiselect'));

      expect(lessonsSelect.componentInstance.options).toEqual(mockLessons);
    });
  });
});
