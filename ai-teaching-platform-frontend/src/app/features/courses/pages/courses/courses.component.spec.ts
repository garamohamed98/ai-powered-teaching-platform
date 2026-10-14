import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CoursesComponent } from './courses.component';
import {CoursesService} from '../../services/courses.service';
import {provideNoopAnimations} from '@angular/platform-browser/animations';
import {provideHttpClient} from '@angular/common/http';
import {Component, input, output} from '@angular/core';
import {Course} from '../../models/course.model';
import {CoursesTableComponent} from '../../components/courses-table/courses-table.component';
import {CoursesFormModalComponent} from '../../components/courses-form-modal/courses-form-modal.component';
import {of, Subject, throwError} from 'rxjs';
import {By} from '@angular/platform-browser';


@Component({selector:'app-courses-table',template:''})
class StubCoursesTableComponent{
  maxRows = input<number>();
  courses = input<Course[]>([]);
  loading = input<boolean>(false);
  courseDeleted = output<void>();
}

@Component({selector:'app-courses-form-modal',template:''})
class StubCoursesFormModalComponent {
  isVisible = input<boolean>(false);
  isVisibleChange = output<boolean>();
  courseCreated = output<void>();
}

describe('CoursesComponent', () => {
  let component: CoursesComponent;
  let fixture: ComponentFixture<CoursesComponent>;
  let coursesService: jasmine.SpyObj<CoursesService>;

  const mockCourses: Course[] = [
    {id:"1", title:'Angular', content:"Angular"},
    {id:"2", title:'Spring Boot', content:"Angular"},
    {id:"3", title:'Java', content:"Java"},
  ]

  beforeEach(async () => {

    coursesService = jasmine.createSpyObj(
      'CoursesService',
      ['getCourses']
    )

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
      .overrideComponent(CoursesComponent,{
        remove:{
          imports: [CoursesTableComponent, CoursesFormModalComponent],
          providers: [CoursesService],
        },
        add:{
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

  describe('loadCourses',()=>{

    it('should be called on init',()=>{
      spyOn(component,'loadCourses')
      fixture.detectChanges();
      expect(component.loadCourses).toHaveBeenCalled();
    })

    it('should show loading state with placeholders while pending',()=>{
      const pending$ = new Subject<Course[]>();
      coursesService.getCourses.and.returnValue(
        pending$.asObservable()
      );

      fixture.detectChanges();

      expect(component.loading()).toBeTrue();
      expect(component.courses().length).toBe(component.maxRows);

    })

    it('should set courses and stop loading on success',()=>{
      fixture.detectChanges();

      expect(component.courses()).toEqual(mockCourses);
      expect(component.loading()).toBeFalse();
    })

    it('should clear courses and stop loading on error',()=>{
      coursesService.getCourses.and.returnValue(
        throwError(()=> new Error('fail'))
      );

      fixture.detectChanges();

      expect(component.courses()).toEqual([]);
      expect(component.loading()).toBeFalse();
    })

  })

  describe('modal',()=>{

    it('showCreateCourseModal should set visibility to true',()=>{
      fixture.detectChanges();
      component.showCreateCourseModal();
      expect(component.isCreateModalVisible()).toBeTrue();
    })

    it('should open the modal when the "new Course" button is clicked',()=>{
      fixture.detectChanges();
      const button: HTMLButtonElement =
        fixture.nativeElement.querySelector('[data-testid="new-course"] button');

      button.click();

      expect(component.isCreateModalVisible()).toBeTrue();
    })

    it('should update visibility when the modal emits isVisibleChange',()=>{
      fixture.detectChanges();
      component.isCreateModalVisible.set(true);
      const modal = fixture.debugElement.query(
        By.directive(StubCoursesFormModalComponent)
      )

      modal.triggerEventHandler('isVisibleChange',false);

      expect(component.isCreateModalVisible()).toBeFalse();
    })

    it('should reload courses when a course is created',()=>{
      fixture.detectChanges();
      coursesService.getCourses.calls.reset();
      const modal = fixture.debugElement.query(
        By.directive(StubCoursesFormModalComponent)
      )
      modal.triggerEventHandler('courseCreated');

      expect(coursesService.getCourses).toHaveBeenCalledTimes(1);
    })

  })

  describe('modal',()=>{
    it('should pass inputs to the table',()=>{
      fixture.detectChanges();
      const table = fixture.debugElement.query(
        By.directive(StubCoursesTableComponent)
      ).componentInstance as StubCoursesTableComponent;

      expect(table.courses()).toEqual(mockCourses);
      expect(table.maxRows()).toBe(component.maxRows);
      expect(table.loading()).toBeFalse();
    })

    it('should reload courses when a course is deleted',()=>{
      fixture.detectChanges();
      coursesService.getCourses.calls.reset();
      const table = fixture.debugElement.query(
        By.directive(StubCoursesTableComponent)
      )

      table.triggerEventHandler('courseDeleted');

      expect(coursesService.getCourses).toHaveBeenCalledTimes(1);
    })
  })

});
