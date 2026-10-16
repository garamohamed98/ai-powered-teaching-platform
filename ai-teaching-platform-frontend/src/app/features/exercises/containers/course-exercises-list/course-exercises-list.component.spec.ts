import { Component, input, output } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { of, Subject, throwError } from 'rxjs';
import { CourseExercisesListComponent } from './course-exercises-list.component';
import { ExercisesService } from '../../services/exercises.service';
import { Exercise } from '../../models/exercise.model';
import { Lesson } from '../../models/lesson.model';
import { ExercisesTableComponent } from '../../components/exercises-table/exercises-table.component';
import { CreateExerciseFormModalComponent } from '../../components/create-exercise-form-modal/create-exercise-form-modal.component';
import { GenerateExerciseFormModalComponent } from '../../components/generate-exercise-form-modal/generate-exercise-form-modal.component';

@Component({ selector: 'app-exercises-table', template: '' })
class StubExercisesTableComponent {
  maxRows = input<number>();
  exercises = input<Exercise[]>([]);
  loading = input<boolean>(false);
}

@Component({ selector: 'app-create-exercise-form-modal', template: '' })
class StubCreateExerciseFormModalComponent {
  isVisible = input<boolean>(false);
  lessons = input<Lesson[]>([]);
  isVisibleChange = output<boolean>();
  exerciseCreated = output<void>();
}

@Component({ selector: 'app-generate-exercise-form-modal', template: '' })
class StubGenerateExerciseFormModalComponent {
  isVisible = input<boolean>(false);
  lessons = input<Lesson[]>([]);
  isVisibleChange = output<boolean>();
  exerciseGenerated = output<void>();
}

describe('CourseExercisesListComponent', () => {
  let component: CourseExercisesListComponent;
  let fixture: ComponentFixture<CourseExercisesListComponent>;
  let exercisesService: jasmine.SpyObj<ExercisesService>;

  const mockLessons: Lesson[] = [
    { id: 'lesson-1', title: 'Introduction' },
    { id: 'lesson-2', title: 'Advanced topics' },
  ];

  const mockExercises: Exercise[] = [
    {
      id: 'exercise-1',
      title: 'Introduction quiz',
      type: 'MULTIPLE_CHOICE',
      lessonList: [mockLessons[0]],
      instructions: 'Choose an answer',
      content: {},
    },
    {
      id: 'exercise-2',
      title: 'Fill in the gaps',
      type: 'FILL_IN_BLANK',
      lessonList: [mockLessons[1]],
      instructions: 'Complete the sentence',
      content: {},
    },
  ];

  beforeEach(async () => {
    exercisesService = jasmine.createSpyObj(
      'ExercisesService',
      ['getExercises'],
    );
    exercisesService.getExercises.and.returnValue(of(mockExercises));

    await TestBed.configureTestingModule({
      imports: [CourseExercisesListComponent],
      providers: [
        provideNoopAnimations(),
        { provide: ExercisesService, useValue: exercisesService },
      ],
    })
      .overrideComponent(CourseExercisesListComponent, {
        remove: {
          imports: [
            ExercisesTableComponent,
            CreateExerciseFormModalComponent,
            GenerateExerciseFormModalComponent,
          ],
        },
        add: {
          imports: [
            StubExercisesTableComponent,
            StubCreateExerciseFormModalComponent,
            StubGenerateExerciseFormModalComponent,
          ],
        },
      })
      .compileComponents();

    fixture = TestBed.createComponent(CourseExercisesListComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('courseId', 'course-1');
    fixture.componentRef.setInput('lessons', mockLessons);
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  describe('loadExercises', () => {
    it('should be called on init', () => {
      spyOn(component, 'loadExercises');

      fixture.detectChanges();

      expect(component.loadExercises).toHaveBeenCalled();
    });

    it('should not load exercises when the course id is empty', () => {
      fixture.componentRef.setInput('courseId', '');

      fixture.detectChanges();

      expect(exercisesService.getExercises).not.toHaveBeenCalled();
    });

    it('should show loading placeholders while the request is pending', () => {
      const pending$ = new Subject<Exercise[]>();
      exercisesService.getExercises.and.returnValue(pending$.asObservable());

      fixture.detectChanges();

      expect(component.loading()).toBeTrue();
      expect(component.exercises().length).toBe(component.maxRows);
    });

    it('should set exercises and stop loading on success', () => {
      fixture.detectChanges();

      expect(exercisesService.getExercises).toHaveBeenCalledWith('course-1');
      expect(component.exercises()).toEqual(mockExercises);
      expect(component.loading()).toBeFalse();
    });

    it('should clear exercises and stop loading on error', () => {
      exercisesService.getExercises.and.returnValue(
        throwError(() => new Error('fail')),
      );

      fixture.detectChanges();

      expect(component.exercises()).toEqual([]);
      expect(component.loading()).toBeFalse();
    });
  });

  describe('exercise modals', () => {
    it('should show the create modal when the create button is clicked', () => {
      fixture.detectChanges();
      const button: HTMLButtonElement = fixture.nativeElement.querySelector(
        '.exercise-list-controller p-button button',
      );

      button.click();

      expect(component.isCreateExerciseModalVisible()).toBeTrue();
    });

    it('should show the generate modal when the generate button is clicked', () => {
      fixture.detectChanges();
      const buttons: NodeListOf<HTMLButtonElement> =
        fixture.nativeElement.querySelectorAll('.exercise-list-controller p-button button');

      buttons[1].click();

      expect(component.isGenerateExerciseModalVisible()).toBeTrue();
    });

    it('should update create modal visibility when the modal emits isVisibleChange', () => {
      fixture.detectChanges();
      const modal = fixture.debugElement.query(
        By.directive(StubCreateExerciseFormModalComponent),
      );

      modal.triggerEventHandler('isVisibleChange', false);

      expect(component.isCreateExerciseModalVisible()).toBeFalse();
    });

    it('should update generate modal visibility when the modal emits isVisibleChange', () => {
      fixture.detectChanges();
      const modal = fixture.debugElement.query(
        By.directive(StubGenerateExerciseFormModalComponent),
      );

      modal.triggerEventHandler('isVisibleChange', false);

      expect(component.isGenerateExerciseModalVisible()).toBeFalse();
    });

    it('should reload exercises when an exercise is created', () => {
      fixture.detectChanges();
      exercisesService.getExercises.calls.reset();
      const modal = fixture.debugElement.query(
        By.directive(StubCreateExerciseFormModalComponent),
      );

      modal.triggerEventHandler('exerciseCreated');

      expect(exercisesService.getExercises).toHaveBeenCalledOnceWith('course-1');
    });

    it('should reload exercises when an exercise is generated', () => {
      fixture.detectChanges();
      exercisesService.getExercises.calls.reset();
      const modal = fixture.debugElement.query(
        By.directive(StubGenerateExerciseFormModalComponent),
      );

      modal.triggerEventHandler('exerciseGenerated');

      expect(exercisesService.getExercises).toHaveBeenCalledOnceWith('course-1');
    });
  });

  describe('child component inputs', () => {
    it('should pass exercises and loading state to the table', () => {
      fixture.detectChanges();
      const table = fixture.debugElement.query(
        By.directive(StubExercisesTableComponent),
      ).componentInstance as StubExercisesTableComponent;

      expect(table.exercises()).toEqual(mockExercises);
      expect(table.maxRows()).toBe(component.maxRows);
      expect(table.loading()).toBeFalse();
    });

    it('should pass lessons to both exercise modals', () => {
      fixture.detectChanges();
      const createModal = fixture.debugElement.query(
        By.directive(StubCreateExerciseFormModalComponent),
      ).componentInstance as StubCreateExerciseFormModalComponent;
      const generateModal = fixture.debugElement.query(
        By.directive(StubGenerateExerciseFormModalComponent),
      ).componentInstance as StubGenerateExerciseFormModalComponent;

      expect(createModal.lessons()).toEqual(mockLessons);
      expect(generateModal.lessons()).toEqual(mockLessons);
    });
  });
});
