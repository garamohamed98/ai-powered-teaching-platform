import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { LessonsFormModalComponent } from './lessons-form-modal.component';
import { CoursesService } from '../../services/courses.service';
import { Lesson } from '../../models/lesson.model';

describe('LessonsFormModalComponent', () => {
  let component: LessonsFormModalComponent;
  let fixture: ComponentFixture<LessonsFormModalComponent>;
  let coursesService: jasmine.SpyObj<CoursesService>;
  let messageService: jasmine.SpyObj<MessageService>;

  const mockLesson: Lesson = {
    id: 'lesson-1',
    title: 'Introduction',
    course_id: 'course-1',
    content: '',
  };

  beforeEach(async () => {
    coursesService = jasmine.createSpyObj(
      'CoursesService',
      ['createLesson'],
    );

    messageService = jasmine.createSpyObj(
      'MessageService',
      ['add'],
    );

    coursesService.createLesson.and.returnValue(
      of(new HttpResponse({ body: mockLesson })),
    );

    await TestBed.configureTestingModule({
      imports: [LessonsFormModalComponent],
      providers: [
        provideNoopAnimations(),
        { provide: CoursesService, useValue: coursesService },
        { provide: MessageService, useValue: messageService },
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(LessonsFormModalComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('isVisible', false);
    fixture.componentRef.setInput('courseId', 'course-1');
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  describe('form validation', () => {
    it('should be invalid when the title is empty', () => {
      fixture.detectChanges();
      component.form.setValue({ title: '' });

      expect(component.form.valid).toBeFalse();
    });

    it('should be invalid when the title has less than 3 characters', () => {
      fixture.detectChanges();
      component.form.setValue({ title: 'Ab' });

      expect(component.form.valid).toBeFalse();
    });

    it('should be valid when the title has 3 or more characters', () => {
      fixture.detectChanges();
      component.form.setValue({ title: 'Angular' });

      expect(component.form.valid).toBeTrue();
    });

    it('should not show the validation message at first', () => {
      fixture.detectChanges();

      expect(fixture.nativeElement.textContent)
        .not.toContain('More then 3 character in lesson title is required');
    });

    it('should show the validation message when the title is touched and invalid', () => {
      fixture.componentRef.setInput('isVisible', true);
      component.form.get('title')?.markAsTouched();
      fixture.detectChanges();

      expect(fixture.nativeElement.textContent)
        .toContain('More then 3 character in lesson title is required');
    });
  });

  describe('submit', () => {
    it('should not call createLesson when the form is invalid', () => {
      fixture.detectChanges();
      component.form.setValue({ title: '' });

      component.submit();

      expect(coursesService.createLesson).not.toHaveBeenCalled();
    });

    it('should call createLesson with the course id and title', () => {
      fixture.detectChanges();
      component.form.setValue({ title: 'Angular' });

      component.submit();

      expect(coursesService.createLesson).toHaveBeenCalledWith('course-1', 'Angular');
    });

    it('should not call createLesson when the course id is empty', () => {
      fixture.componentRef.setInput('courseId', '');
      fixture.detectChanges();
      component.form.setValue({ title: 'Angular' });

      component.submit();

      expect(coursesService.createLesson).not.toHaveBeenCalled();
    });

    it('should display a success message after creation', () => {
      fixture.detectChanges();
      component.form.setValue({ title: 'Angular' });

      component.submit();

      expect(messageService.add).toHaveBeenCalledWith(
        jasmine.objectContaining({
          severity: 'success',
          detail: 'New Lesson is created with title Introduction',
        }),
      );
    });

    it('should emit lessonCreated after creation', () => {
      fixture.detectChanges();
      spyOn(component.lessonCreated, 'emit');
      component.form.setValue({ title: 'Angular' });

      component.submit();

      expect(component.lessonCreated.emit).toHaveBeenCalled();
    });

    it('should close the modal after creation', () => {
      fixture.detectChanges();
      spyOn(component.isVisibleChange, 'emit');
      component.form.setValue({ title: 'Angular' });

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
});
