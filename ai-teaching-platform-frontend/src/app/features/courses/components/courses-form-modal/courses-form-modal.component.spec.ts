import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CoursesFormModalComponent } from './courses-form-modal.component';
import { CoursesService } from '../../services/courses.service';
import { MessageService } from 'primeng/api';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { CourseCategoryService } from '../../services/course-category.service';
import { CourseCategory } from '../../models/course-category.model';
import { By } from '@angular/platform-browser';
import { CreateCourseDto } from "../../models/create-course.dto";
import { UpdateCourseDto } from "../../models/update-course.dto";
import { Course } from "../../models/course.model";
import { provideHttpClient } from '@angular/common/http';

describe('CoursesFormModalComponent', () => {
  let component: CoursesFormModalComponent;
  let fixture: ComponentFixture<CoursesFormModalComponent>;
  let coursesService: jasmine.SpyObj<CoursesService>;
  let courseCategoryService: jasmine.SpyObj<CourseCategoryService>;
  let messageService: jasmine.SpyObj<MessageService>;

  const mockCategories: CourseCategory[] = [
    { id: 'cat-1', name: 'Software Engineering', slug: 'software-engineering', displayOrder: 1 },
    { id: 'cat-2', name: 'Data Science', slug: 'data-science', displayOrder: 2 }
  ];

  const mockSelectedCourse: Course = {
    id: 'course-123',
    title: 'Existing Course',
    category: mockCategories[0],
    visibility: 'PUBLIC',
    createdAt: new Date().toISOString()
  };

  beforeEach(async () => {
    coursesService = jasmine.createSpyObj(
      'CoursesService',
      ['createCourse', 'updateCourse']
    );

    courseCategoryService = jasmine.createSpyObj(
      'CourseCategoryService',
      ['getCourseCategoryList']
    );

    messageService = jasmine.createSpyObj(
      'MessageService',
      ['add']
    );

    coursesService.createCourse.and.returnValue(of({
      id: "1",
      title: "Ai",
      category: mockCategories[0],
      visibility: "PUBLIC",
      createdAt: (new Date()).toISOString(),
    }));

    coursesService.updateCourse.and.returnValue(of(mockSelectedCourse));

    courseCategoryService.getCourseCategoryList.and.returnValue(of(mockCategories));

    await TestBed.configureTestingModule({
      imports: [CoursesFormModalComponent],
      providers: [
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

  describe('form Initial State (Create Mode - selectedCourse is null)', () => {
    beforeEach(() => {
      component.selectedCourse = null;
      fixture.detectChanges();
    });

    it('should initialize form with empty title, null categoryId, and PUBLIC visibility', () => {
      expect(component.form.get('title')?.value).toBe('');
      expect(component.form.get('categoryId')?.value).toBeNull();
      expect(component.form.get('visibility')?.value).toBe('PUBLIC');
    });

    it('should start with an invalid form', () => {
      expect(component.form.valid).toBeFalse();
    });
  });

  describe('form Initial State (Edit Mode - selectedCourse is provided)', () => {
    beforeEach(() => {
      component.selectedCourse = mockSelectedCourse;
      component.loadForm();
      fixture.detectChanges();
    });

    it('should initialize form with values from selectedCourse', () => {
      expect(component.form.get('title')?.value).toBe(mockSelectedCourse.title);
      expect(component.form.get('categoryId')?.value).toBe(mockSelectedCourse.category.id);
      expect(component.form.get('visibility')?.value).toBe(mockSelectedCourse.visibility);
    });

    it('should start with a valid form when prefilled', () => {
      expect(component.form.valid).toBeTrue();
    });
  });

  describe('form validation and error messages', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

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

    it('should be valid when the title has 3 or more characters and categoryId is selected', () => {
      component.form.setValue({
        title: 'Angular',
        categoryId: mockCategories[0].id,
        visibility: 'PUBLIC'
      });

      expect(component.form.valid).toBeTrue();
    });
  });

  describe('form submission (Create Mode)', () => {
    beforeEach(() => {
      component.selectedCourse = null;
      fixture.detectChanges();
    });

    it('should not call createCourse when the form is invalid', () => {
      component.form.setValue({
        title: '',
        categoryId: mockCategories[0].id,
        visibility: 'PUBLIC'
      });

      component.submit();

      expect(coursesService.createCourse).not.toHaveBeenCalled();
    });

    it('should call createCourse with the DTO', () => {
      const createCourseDto: CreateCourseDto = {
        title: 'Angular',
        categoryId: mockCategories[0].id,
        visibility: 'PUBLIC'
      };

      component.form.setValue({
        title: createCourseDto.title,
        categoryId: createCourseDto.categoryId,
        visibility: 'PUBLIC'
      });

      component.submit();

      expect(coursesService.createCourse).toHaveBeenCalledWith(createCourseDto);
    });

    it('should display a success message after creation', () => {
      component.form.setValue({
        title: 'Angular',
        categoryId: mockCategories[0].id,
        visibility: 'PUBLIC'
      });

      component.submit();

      expect(messageService.add).toHaveBeenCalledWith(
        jasmine.objectContaining({ severity: 'success' })
      );
    });

    it('should emit courseSaved after creation', () => {
      spyOn(component.courseSaved, 'emit');
      component.form.setValue({
        title: 'Angular',
        categoryId: mockCategories[0].id,
        visibility: 'PUBLIC'
      });

      component.submit();

      expect(component.courseSaved.emit).toHaveBeenCalled();
    });

    it('should close the modal after creation', () => {
      spyOn(component.isVisibleChange, 'emit');
      component.form.setValue({
        title: 'Angular',
        categoryId: mockCategories[0].id,
        visibility: 'PUBLIC'
      });

      component.submit();

      expect(component.isVisible).toBeFalse();
      expect(component.isVisibleChange.emit).toHaveBeenCalledWith(false);
    });
  });

  describe('form submission (Edit/Update Mode)', () => {
    beforeEach(() => {
      component.selectedCourse = mockSelectedCourse;
      component.loadForm();
      fixture.detectChanges();
    });

    it('should call updateCourse with the modified fields DTO', () => {
      component.form.patchValue({
        title: 'Updated Angular Title',
        visibility: 'PRIVATE'
      });

      const expectedUpdateDto: UpdateCourseDto = {
        title: 'Updated Angular Title',
        categoryId: undefined,
        visibility: 'PRIVATE'
      };

      component.submit();

      expect(coursesService.updateCourse).toHaveBeenCalledWith(mockSelectedCourse.id, expectedUpdateDto);
    });

    it('should display a success message after update', () => {
      component.form.patchValue({ title: 'Updated Angular Title' });

      component.submit();

      expect(messageService.add).toHaveBeenCalledWith(
        jasmine.objectContaining({ severity: 'success' })
      );
    });

    it('should emit courseSaved after update', () => {
      spyOn(component.courseSaved, 'emit');
      component.form.patchValue({ title: 'Updated Angular Title' });

      component.submit();

      expect(component.courseSaved.emit).toHaveBeenCalled();
    });

    it('should close the modal after update', () => {
      spyOn(component.isVisibleChange, 'emit');
      component.form.patchValue({ title: 'Updated Angular Title' });

      component.submit();

      expect(component.isVisible).toBeFalse();
      expect(component.isVisibleChange.emit).toHaveBeenCalledWith(false);
    });
  });

  describe('Modal Actions & Closing', () => {
    it('should emit isVisibleChange with false when closed', () => {
      fixture.detectChanges();
      spyOn(component.isVisibleChange, 'emit');

      component.close();

      expect(component.isVisible).toBeFalse();
      expect(component.isVisibleChange.emit).toHaveBeenCalledWith(false);
    });
  });
});
