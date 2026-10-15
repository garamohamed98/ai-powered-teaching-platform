import { Component, input, output } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { By } from '@angular/platform-browser';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { of, Subject, throwError } from 'rxjs';
import { CourseDetailsComponent } from './course-details.component';
import { CoursesService } from '../../services/courses.service';
import { LessonsService } from '../../services/lessons.service';
import { Lesson } from '../../models/lesson.model';
import { LessonEditorComponent } from '../../components/lesson-editor/lesson-editor.component';
import { LessonsPanelComponent } from '../../components/lessons-panel/lessons-panel.component';
import { LessonsFormModalComponent } from '../../components/lessons-form-modal/lessons-form-modal.component';
import { CourseExercisesListComponent } from '../../../exercises';

@Component({ selector: 'app-lesson-editor', template: '' })
class StubLessonEditorComponent {
  lesson = input<Lesson | null>(null);
  loading = input<boolean>(false);
  onCourseContentChanged = output<string>();
}

@Component({ selector: 'app-lessons-panel', template: '' })
class StubLessonsPanelComponent {
  lessons = input<Lesson[]>([]);
  lessonSelected = output<Lesson>();
}

@Component({ selector: 'app-lessons-form-modal', template: '' })
class StubLessonsFormModalComponent {
  isVisible = input<boolean>(false);
  courseId = input<string>('');
  isVisibleChange = output<boolean>();
  lessonCreated = output<void>();
}

@Component({ selector: 'app-course-exercises-list', template: '' })
class StubCourseExercisesListComponent {
  courseId = input<string>('');
  lessons = input<Lesson[]>([]);
}

describe('CourseDetailsComponent', () => {
  let component: CourseDetailsComponent;
  let fixture: ComponentFixture<CourseDetailsComponent>;
  let coursesService: jasmine.SpyObj<CoursesService>;
  let lessonsService: jasmine.SpyObj<LessonsService>;
  let router: jasmine.SpyObj<Router>;
  let routeId: string | null;

  const mockLessons: Lesson[] = [
    { id: 'lesson-1', title: 'Introduction', course_id: 'course-1', content: 'Welcome' },
    { id: 'lesson-2', title: 'Advanced topics', course_id: 'course-1', content: 'Details' },
  ];

  beforeEach(async () => {
    routeId = 'course-1';
    coursesService = jasmine.createSpyObj('CoursesService', ['getLessonsByCourseId']);
    lessonsService = jasmine.createSpyObj('LessonsService', ['getLesson']);
    router = jasmine.createSpyObj('Router', ['navigate']);

    coursesService.getLessonsByCourseId.and.returnValue(of(mockLessons));
    lessonsService.getLesson.and.returnValue(of(mockLessons[0]));

    await TestBed.configureTestingModule({
      imports: [CourseDetailsComponent],
      providers: [
        provideNoopAnimations(),
        { provide: CoursesService, useValue: coursesService },
        { provide: LessonsService, useValue: lessonsService },
        { provide: Router, useValue: router },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => routeId } } },
        },
      ],
    })
      .overrideComponent(CourseDetailsComponent, {
        remove: {
          imports: [
            LessonEditorComponent,
            LessonsPanelComponent,
            LessonsFormModalComponent,
            CourseExercisesListComponent,
          ],
          providers: [CoursesService],
        },
        add: {
          imports: [
            StubLessonEditorComponent,
            StubLessonsPanelComponent,
            StubLessonsFormModalComponent,
            StubCourseExercisesListComponent,
          ],
        },
      })
      .compileComponents();

    fixture = TestBed.createComponent(CourseDetailsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  describe('route initialization', () => {
    it('should set the course id and load its lessons on init', () => {
      fixture.detectChanges();

      expect(component.courseId).toBe('course-1');
      expect(component.course().id).toBe('course-1');
      expect(coursesService.getLessonsByCourseId).toHaveBeenCalledWith('course-1');
      expect(component.lessons()).toEqual(mockLessons);
    });

    it('should navigate to not found when the route has no course id', () => {
      routeId = null;
      fixture.detectChanges();

      expect(router.navigate).toHaveBeenCalledWith(['/not-found']);
      expect(coursesService.getLessonsByCourseId).not.toHaveBeenCalled();
    });
  });

  describe('loadLessons', () => {
    it('should show loading placeholders while the request is pending', () => {
      const pending$ = new Subject<Lesson[]>();
      coursesService.getLessonsByCourseId.and.returnValue(pending$.asObservable());

      fixture.detectChanges();

      expect(component.loading()).toBeTrue();
      expect(component.lessons().length).toBe(component.maxLessonsRow);
    });

    it('should set lessons and stop loading on success', () => {
      fixture.detectChanges();

      expect(component.lessons()).toEqual(mockLessons);
      expect(component.loading()).toBeFalse();
    });

    it('should clear lessons and stop loading on error', () => {
      coursesService.getLessonsByCourseId.and.returnValue(
        throwError(() => new Error('fail')),
      );

      fixture.detectChanges();

      expect(component.lessons()).toEqual([]);
      expect(component.loading()).toBeFalse();
    });
  });

  describe('lesson selection', () => {
    it('should not load a lesson when its id is empty', () => {
      fixture.detectChanges();
      component.lessonIsLoading.set(false);

      component.loadLesson('');

      expect(lessonsService.getLesson).not.toHaveBeenCalled();
      expect(component.lessonIsLoading()).toBeFalse();
    });

    it('should set the selected lesson and stop loading on success', () => {
      fixture.detectChanges();

      component.loadLesson('lesson-1');

      expect(lessonsService.getLesson).toHaveBeenCalledWith('lesson-1');
      expect(component.loadedLesson()).toEqual(mockLessons[0]);
      expect(component.lessonIsLoading()).toBeFalse();
    });

    it('should stop loading when fetching the selected lesson fails', () => {
      lessonsService.getLesson.and.returnValue(throwError(() => new Error('fail')));
      fixture.detectChanges();

      component.loadLesson('lesson-1');

      expect(component.lessonIsLoading()).toBeFalse();
    });

    it('should load the selected lesson when the lessons panel emits a selection', () => {
      fixture.detectChanges();
      const panel = fixture.debugElement.query(
        By.directive(StubLessonsPanelComponent),
      );

      panel.triggerEventHandler('lessonSelected', mockLessons[0]);

      expect(lessonsService.getLesson).toHaveBeenCalledWith('lesson-1');
    });
  });

  describe('lesson content', () => {
    it('should update the loaded lesson content', () => {
      fixture.detectChanges();
      component.loadedLesson.set(mockLessons[0]);

      component.handleCourseContentChanged('Updated content');

      expect(component.loadedLesson()).toEqual({
        ...mockLessons[0],
        content: 'Updated content',
      });
    });

    it('should keep the loaded lesson null when content changes before selection', () => {
      fixture.detectChanges();

      component.handleCourseContentChanged('Updated content');

      expect(component.loadedLesson()).toBeNull();
    });
  });

  describe('create lesson modal', () => {
    it('should show the modal when the new lesson button is clicked', () => {
      fixture.detectChanges();
      const button: HTMLButtonElement = fixture.nativeElement.querySelector('p-button button');

      button.click();

      expect(component.isCreateLessonModalVisible()).toBeTrue();
    });

    it('should update modal visibility when the modal emits isVisibleChange', () => {
      fixture.detectChanges();
      const modal = fixture.debugElement.query(
        By.directive(StubLessonsFormModalComponent),
      );

      modal.triggerEventHandler('isVisibleChange', false);

      expect(component.isCreateLessonModalVisible()).toBeFalse();
    });

    it('should reload lessons when a lesson is created', () => {
      fixture.detectChanges();
      coursesService.getLessonsByCourseId.calls.reset();
      const modal = fixture.debugElement.query(
        By.directive(StubLessonsFormModalComponent),
      );

      modal.triggerEventHandler('lessonCreated');

      expect(coursesService.getLessonsByCourseId).toHaveBeenCalledOnceWith('course-1');
    });
  });

  describe('child component inputs', () => {
    it('should pass lessons and course id to the child components', () => {
      fixture.detectChanges();
      const panel = fixture.debugElement.query(
        By.directive(StubLessonsPanelComponent),
      ).componentInstance as StubLessonsPanelComponent;
      const exerciseList = fixture.debugElement.query(
        By.directive(StubCourseExercisesListComponent),
      ).componentInstance as StubCourseExercisesListComponent;
      const modal = fixture.debugElement.query(
        By.directive(StubLessonsFormModalComponent),
      ).componentInstance as StubLessonsFormModalComponent;

      expect(panel.lessons()).toEqual(mockLessons);
      expect(exerciseList.courseId()).toBe('course-1');
      expect(exerciseList.lessons()).toEqual(mockLessons);
      expect(modal.courseId()).toBe('course-1');
    });

    it('should pass the selected lesson and loading state to the editor', () => {
      fixture.detectChanges();
      component.loadedLesson.set(mockLessons[0]);
      component.lessonIsLoading.set(true);
      fixture.detectChanges();
      const editor = fixture.debugElement.query(
        By.directive(StubLessonEditorComponent),
      ).componentInstance as StubLessonEditorComponent;

      expect(editor.lesson()).toEqual(mockLessons[0]);
      expect(editor.loading()).toBeTrue();
    });
  });
});
