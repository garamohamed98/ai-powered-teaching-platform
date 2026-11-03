import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CoursesFormModalComponent } from './courses-form-modal.component';
import {CoursesService} from '../../services/courses.service';
import {MessageService} from 'primeng/api';
import {provideNoopAnimations} from '@angular/platform-browser/animations';
import {of} from 'rxjs';
import {CourseCategoryService} from '../../services/course-category.service';
import {CourseCategory} from '../../models/course-category.model';
import {By} from '@angular/platform-browser';
import { CreateCourseDto } from "../../models/create-course.dto";
import {provideHttpClient} from '@angular/common/http';

describe('CoursesFormModalComponent', () => {
  let component: CoursesFormModalComponent;
  let fixture: ComponentFixture<CoursesFormModalComponent>;
  let coursesService: jasmine.SpyObj<CoursesService>;
  let courseCategoryService: jasmine.SpyObj<CourseCategoryService>;
  let messageService: jasmine.SpyObj<MessageService>;

  const mockCategories:CourseCategory[] = [
    {id: 'cat-1', name:'Software Engineering',slug:'software-engineering', displayOrder:1},
    {id: 'cat-2', name:'Data science',slug:'data-science', displayOrder:2}
  ]

  beforeEach(async () => {
    coursesService = jasmine.createSpyObj(
      'CoursesService',
      ['createCourse']
    )

    courseCategoryService = jasmine.createSpyObj(
      'CourseCategoryService',
      ['getCourseCategoryList']
    )

    messageService = jasmine.createSpyObj(
      'MessageService',
      ['add']
    )

    coursesService.createCourse.and.returnValue(of({
      id:"1",
      title:"Ai",
      category: {id:"1",name:"Software Engineering",slug:"software-engineering",displayOrder:1},
      visibility:"PUBLIC",
      createdAt: (new Date()).toISOString(),
    }))

    courseCategoryService.getCourseCategoryList.and.returnValue(of(mockCategories))

    await TestBed.configureTestingModule({
      imports: [CoursesFormModalComponent],
      providers:[
        provideNoopAnimations(),
        provideHttpClient(),
        {
          provide: CoursesService,
          useValue: coursesService
        },
        {
          provide: CourseCategoryService,
          useValue: courseCategoryService
        },
        {
          provide: MessageService,
          useValue: messageService
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CoursesFormModalComponent);
    component = fixture.componentInstance;

    component.isVisible = true;
  });

  it('should create and load categories on init', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(courseCategoryService.getCourseCategoryList).toHaveBeenCalled();
    expect(component.categoryList()).toEqual(mockCategories);
  });

  describe('form Initial State',()=>{

    beforeEach(()=>{
      fixture.detectChanges();
    })

    it('should initialize form with empty title, null categoryId, and PUBLIC visibility',()=>{
      expect(component.form.get('title')?.value).toBe('');
      expect(component.form.get('categoryId')?.value).toBeNull();
      expect(component.form.get('visibility')?.value).toBe('PUBLIC');
    })

    it('should start with an invalid form',()=>{
      expect(component.form.valid).toBeFalse();
    })
  })

  describe('form validation and error messages', () => {

    it('should show required error when title is touched and empty', () => {
      const titleControl = component.form.get('title');
      titleControl?.markAsTouched();
      fixture.detectChanges();

      const errorElement = fixture.debugElement.query(By.css('.modal-error-container'));
      expect(errorElement).not.toBeNull();
      expect(errorElement.nativeElement.textContent).toContain('Course title is required.');
    });

    it('should show minlength error when title has less than 3 characters', () => {
      const titleControl = component.form.get('title');
      titleControl?.setValue('ab');
      titleControl?.markAsTouched();
      fixture.detectChanges();

      const errorElement = fixture.debugElement.query(By.css('.modal-error-container'));
      expect(errorElement).not.toBeNull();
      expect(errorElement.nativeElement.textContent).toContain('Course title must contain at least 3 characters.');
    });

    it('should show required error when categoryId is touched and null', () => {
      const categoryControl = component.form.get('categoryId');
      categoryControl?.markAsTouched();
      fixture.detectChanges();

      const errorElements = fixture.debugElement.queryAll(By.css('.modal-error-container'));
      const categoryError = errorElements.find(el => el.nativeElement.textContent.includes('Category is required.'));

      expect(categoryError).not.toBeNull();
    });

    it('should be valid when the title has 3 or more characters a,d categoryId is selected',()=>{
      fixture.detectChanges();
      component.form.setValue({
        title:'Angular',
        categoryId: mockCategories[0].id,
        visibility: 'PUBLIC'
      })

      expect(component.form.valid).toBeTrue();
    })

    it('should not show the validation message at first',()=>{
      fixture.detectChanges();

      expect(document.body.textContent)
        .not.toContain('More than 3 characters in course title is required');
    })

  })

  describe('form submission',()=>{
    it('should not call createCourse when the form is invalid',()=>{
      fixture.detectChanges();
      component.form.setValue(
        {
          title:'',
          categoryId:mockCategories[0].id,
          visibility: 'PUBLIC'
        }
      )

      component.submit();

      expect(coursesService.createCourse).not.toHaveBeenCalled();
    })

    it('should call createCourse with the DTO',()=>{
      fixture.detectChanges();

      const createCourseDto: CreateCourseDto = {
        title: 'Angular',
        categoryId: mockCategories[0].id,
        visibility: 'PUBLIC'
      }

      component.form.setValue(
        {
          title:createCourseDto.title,
          categoryId:createCourseDto.categoryId,
          visibility: 'PUBLIC'
        });

      component.submit();

      expect(coursesService.createCourse).toHaveBeenCalledWith(createCourseDto);
    });

    it('should display a success message after creation',()=>{
      fixture.detectChanges();
      component.form.setValue({
        title:'Angular',
        categoryId:mockCategories[0].id,
        visibility: 'PUBLIC'
      });

      component.submit();

      expect(messageService.add).toHaveBeenCalledWith(
        jasmine.objectContaining({severity: 'success'})
      )
    })

    it('should emit courseCreated after creation',()=>{
      fixture.detectChanges();
      spyOn(component.courseCreated, 'emit');
      component.form.setValue({
        title:'Angular',
        categoryId:mockCategories[0].id,
        visibility: 'PUBLIC'
      });

      component.submit();

      expect(component.courseCreated.emit).toHaveBeenCalled();
    })

    it('should close the modal after creation',()=>{
      fixture.detectChanges();
      spyOn(component.isVisibleChange, 'emit');
      component.form.setValue({
        title:'Angular',
        categoryId:mockCategories[0].id,
        visibility: 'PUBLIC'
      });

      component.submit();

      expect(component.isVisible).toBeFalse();
      expect(component.isVisibleChange.emit).toHaveBeenCalledWith(false);
    })
  })

  describe('Modal Actions & Closing',()=>{
    it('should emit isVisibleChange with false when closed',()=>{
      fixture.detectChanges();
      spyOn(component.isVisibleChange, 'emit');

      component.close();

      expect(component.isVisible).toBeFalse();
      expect(component.isVisibleChange.emit).toHaveBeenCalledWith(false);
    })
  })

});
