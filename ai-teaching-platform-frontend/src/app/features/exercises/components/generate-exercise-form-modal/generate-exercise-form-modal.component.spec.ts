import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { GenerateExerciseFormModalComponent } from './generate-exercise-form-modal.component';
import { ExercisesService } from '../../services/exercises.service';
import { Exercise } from '../../models/exercise.model';
import { Lesson } from '../../models/lesson.model';

describe('GenerateExerciseFormModalComponent', () => {
  let component: GenerateExerciseFormModalComponent;
  let fixture: ComponentFixture<GenerateExerciseFormModalComponent>;
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
      ['generateExercise'],
    );
    messageService = jasmine.createSpyObj(
      'MessageService',
      ['add'],
    );
    exercisesService.generateExercise.and.returnValue(of(mockExercise));

    await TestBed.configureTestingModule({
      imports: [GenerateExerciseFormModalComponent],
      providers: [
        provideNoopAnimations(),
        { provide: ExercisesService, useValue: exercisesService },
        { provide: MessageService, useValue: messageService },
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(GenerateExerciseFormModalComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('isVisible', false);
    fixture.componentRef.setInput('lessons', mockLessons);
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  describe('form validation', () => {
    it('should be invalid when the exercise type is not selected', () => {
      fixture.detectChanges();
      component.form.reset();

      expect(component.form.valid).toBeFalse();
    });

    it('should be valid when an exercise type is selected', () => {
      fixture.detectChanges();
      component.form.patchValue({
        exerciseType: { label: 'Multiple Choice', value: 'MULTIPLE_CHOICE' },
      });

      expect(component.form.valid).toBeTrue();
    });

    it('should allow an empty lesson selection', () => {
      fixture.detectChanges();
      component.form.patchValue({
        exerciseType: { label: 'Fill in blank', value: 'FILL_IN_BLANK' },
        lessons: [],
      });

      expect(component.form.valid).toBeTrue();
    });
  });

  describe('submit', () => {
    it('should not call generateExercise when the form is invalid', () => {
      fixture.detectChanges();
      component.form.reset();

      component.submit();

      expect(exercisesService.generateExercise).not.toHaveBeenCalled();
    });

    it('should call generateExercise with the selected type and lesson ids', () => {
      fixture.detectChanges();
      component.form.patchValue({
        exerciseType: { label: 'Multiple Choice', value: 'MULTIPLE_CHOICE' },
        lessons: mockLessons,
      });

      component.submit();

      expect(exercisesService.generateExercise).toHaveBeenCalledWith({
        type: 'MULTIPLE_CHOICE',
        lessonIdList: ['lesson-1', 'lesson-2'],
      });
    });

    it('should call generateExercise with an empty lesson id list when none are selected', () => {
      fixture.detectChanges();
      component.form.patchValue({
        exerciseType: { label: 'Fill in blank', value: 'FILL_IN_BLANK' },
        lessons: [],
      });

      component.submit();

      expect(exercisesService.generateExercise).toHaveBeenCalledWith({
        type: 'FILL_IN_BLANK',
        lessonIdList: [],
      });
    });

    it('should display a success message after generation', () => {
      fixture.detectChanges();
      component.form.patchValue({
        exerciseType: { label: 'Multiple Choice', value: 'MULTIPLE_CHOICE' },
        lessons: mockLessons,
      });

      component.submit();

      expect(messageService.add).toHaveBeenCalledWith(
        jasmine.objectContaining({
          severity: 'success',
          detail: 'Exercise has been created successfully related to lessons Introduction, Advanced topics',
        }),
      );
    });

    it('should emit exerciseGenerated after generation', () => {
      fixture.detectChanges();
      component.form.patchValue({
        exerciseType: { label: 'Multiple Choice', value: 'MULTIPLE_CHOICE' },
        lessons: mockLessons,
      });
      spyOn(component.exerciseGenerated, 'emit');

      component.submit();

      expect(component.exerciseGenerated.emit).toHaveBeenCalled();
    });

    it('should close the modal after generation', () => {
      fixture.detectChanges();
      component.form.patchValue({
        exerciseType: { label: 'Multiple Choice', value: 'MULTIPLE_CHOICE' },
        lessons: mockLessons,
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

  describe('form options', () => {
    it('should provide both supported exercise types', () => {
      fixture.detectChanges();

      expect(component.exerciseTypes).toEqual([
        { label: 'Multiple Choice', value: 'MULTIPLE_CHOICE' },
        { label: 'Fill in blank', value: 'FILL_IN_BLANK' },
      ]);
    });

    it('should pass the available lessons to the lesson selector', () => {
      fixture.componentRef.setInput('isVisible', true);
      fixture.detectChanges();
      const lessonsSelect = fixture.debugElement.query(By.css('p-multiselect'));

      expect(lessonsSelect.componentInstance.options).toEqual(mockLessons);
    });

    it('should pass the exercise types to the type selector', () => {
      fixture.componentRef.setInput('isVisible', true);
      fixture.detectChanges();
      const typeSelect = fixture.debugElement.query(By.css('p-select'));

      expect(typeSelect.componentInstance.options).toEqual(component.exerciseTypes);
    });
  });
});
