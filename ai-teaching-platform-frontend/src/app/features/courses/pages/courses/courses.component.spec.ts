import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CoursesComponent } from './courses.component';
import { CoursesService } from '../../services/courses.service';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient } from '@angular/common/http';
import { Component, input, output } from '@angular/core';
import { Course } from '../../models/course.model';
import { CoursesTableComponent } from '../../components/courses-table/courses-table.component';
import { CoursesFormModalComponent } from '../../components/courses-form-modal/courses-form-modal.component';
import { of, Subject, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';

@Component({ selector: 'app-courses-table', template: '' })
class StubCoursesTableComponent {
  maxRows = input<number>();
  courses = input<Course[]>([]);
  loading = input<boolean>(false);
  courseDeleted = output<void>();
  editCourseClicked = output<Course>();
}

@Component({ selector: 'app-courses-form-modal', template: '' })
class StubCoursesFormModalComponent {
  isVisible = input<boolean>(false);
  selectedCourse = input<Course | null>(null);
  isVisibleChange = output<boolean>();
  courseSaved = output<void>();
}

describe('CoursesComponent', () => {
  let component: CoursesComponent;
  let fixture: ComponentFixture<CoursesComponent>;
  let coursesService: jasmine.SpyObj<CoursesService>;

  const mockCourses = [
    {
      id: '1',
      title: 'Angular Basics',
      category: { name: 'Frontend' },
      visibility: 'PUBLIC',
      createdAt: new Date(2025, 0, 15, 10, 30),
    },
    {
      id: '2',
      title: 'Spring Boot Deep Dive',
      category: { name: 'Backend' },
      visibility: 'PRIVATE',
      createdAt: new Date(2025, 1, 20, 14, 5),
    },
    {
      id: '3',
      title: 'Docker for Beginners',
      category: { name: 'DevOps' },
      visibility: 'PUBLIC',
      createdAt: new Date(2025, 2, 3, 9, 0),
    },
  ] as unknown as Course[];

  beforeEach(async () => {
    coursesService = jasmine.createSpyObj(
      'CoursesService',
      ['getCourses']
    );

    coursesService.getCourses.and.returnValue(of(mockCourses));

    await TestBed.configureTestingModule({
      imports: [CoursesComponent],
      providers: [
        provideNoopAnimations(),
        provideHttpClient(),
        {
          provide: CoursesService,
          useValue: coursesService
        },
      ]
    })
      .overrideComponent(CoursesComponent, {
        remove: {
          imports: [CoursesTableComponent, CoursesFormModalComponent],
          providers: [CoursesService],
        },
        add: {
          imports: [StubCoursesTableComponent, StubCoursesFormModalComponent],
        }
      })
      .compileComponents();

    fixture = TestBed.createComponent(CoursesComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('loadCourses', () => {
    it('should be called on init', () => {
      spyOn(component, 'loadCourses');
      fixture.detectChanges();
      expect(component.loadCourses).toHaveBeenCalled();
    });

    it('should show loading state with placeholders while pending', () => {
      const pending$ = new Subject<Course[]>();
      coursesService.getCourses.and.returnValue(
        pending$.asObservable()
      );

      fixture.detectChanges();

      expect(component.loading()).toBeTrue();
      expect(component.courses().length).toBe(component.maxRows);
    });

    it('should set courses and stop loading on success', () => {
      fixture.detectChanges();

      expect(component.courses()).toEqual(mockCourses);
      expect(component.loading()).toBeFalse();
    });

    it('should clear courses and stop loading on error', () => {
      coursesService.getCourses.and.returnValue(
        throwError(() => new Error('fail'))
      );

      fixture.detectChanges();

      expect(component.courses()).toEqual([]);
      expect(component.loading()).toBeFalse();
    });
  });

  describe('modal and course selection', () => {
    it('showCreateCourseModal should set selectedCourse to null and visibility to true', () => {
      fixture.detectChanges();
      component.selectedCourse = mockCourses[0]; // Set a course initially
      component.showCreateCourseModal();

      expect(component.selectedCourse).toBeNull();
      expect(component.isCourseModalVisible()).toBeTrue();
    });

    it('showEditCourseModal should set selectedCourse and visibility to true', () => {
      fixture.detectChanges();
      const courseToEdit = mockCourses[1];
      component.showEditCourseModal(courseToEdit);

      expect(component.selectedCourse).toEqual(courseToEdit);
      expect(component.isCourseModalVisible()).toBeTrue();
    });

    it('should open the modal with null selectedCourse when the "New Course" button is clicked', () => {
      fixture.detectChanges();
      component.selectedCourse = mockCourses[0];
      const button: HTMLButtonElement =
        fixture.nativeElement.querySelector('[data-testid="new-course"] button');

      button.click();

      expect(component.selectedCourse).toBeNull();
      expect(component.isCourseModalVisible()).toBeTrue();
    });

    it('should update visibility when the modal emits isVisibleChange', () => {
      fixture.detectChanges();
      component.isCourseModalVisible.set(true);
      const modal = fixture.debugElement.query(
        By.directive(StubCoursesFormModalComponent)
      );

      modal.triggerEventHandler('isVisibleChange', false);

      expect(component.isCourseModalVisible()).toBeFalse();
    });

    it('should reload courses when the modal emits courseSaved', () => {
      fixture.detectChanges();
      coursesService.getCourses.calls.reset();
      const modal = fixture.debugElement.query(
        By.directive(StubCoursesFormModalComponent)
      );
      modal.triggerEventHandler('courseSaved');

      expect(coursesService.getCourses).toHaveBeenCalledTimes(1);
    });

    it('should trigger showEditCourseModal and open modal when table emits editCourseClicked', () => {
      fixture.detectChanges();
      const table = fixture.debugElement.query(
        By.directive(StubCoursesTableComponent)
      );
      const courseToEdit = mockCourses[1];

      table.triggerEventHandler('editCourseClicked', courseToEdit);

      expect(component.selectedCourse).toEqual(courseToEdit);
      expect(component.isCourseModalVisible()).toBeTrue();
    });
  });

  describe('table communication', () => {
    it('should pass inputs to the table', () => {
      fixture.detectChanges();
      const table = fixture.debugElement.query(
        By.directive(StubCoursesTableComponent)
      ).componentInstance as StubCoursesTableComponent;

      expect(table.courses()).toEqual(mockCourses);
      expect(table.maxRows()).toBe(component.maxRows);
      expect(table.loading()).toBeFalse();
    });

    it('should reload courses when a course is deleted', () => {
      fixture.detectChanges();
      coursesService.getCourses.calls.reset();
      const table = fixture.debugElement.query(
        By.directive(StubCoursesTableComponent)
      );

      table.triggerEventHandler('courseDeleted');

      expect(coursesService.getCourses).toHaveBeenCalledTimes(1);
    });
  });
});
