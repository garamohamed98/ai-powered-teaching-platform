import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { LessonEditorComponent } from './lesson-editor.component';
import { LessonsService } from '../../services/lessons.service';
import { Lesson } from '../../models/lesson.model';

describe('LessonEditorComponent', () => {
  let component: LessonEditorComponent;
  let fixture: ComponentFixture<LessonEditorComponent>;
  let lessonsService: jasmine.SpyObj<LessonsService>;
  let messageService: jasmine.SpyObj<MessageService>;

  const mockLesson: Lesson = {
    id: 'lesson-1',
    title: 'Introduction',
    course_id: 'course-1',
    content: '<p>Welcome</p>',
  };

  beforeEach(async () => {
    lessonsService = jasmine.createSpyObj(
      'LessonsService',
      ['updateLessonContent'],
    );
    messageService = jasmine.createSpyObj('MessageService', ['add']);

    lessonsService.updateLessonContent.and.returnValue(of(mockLesson));

    await TestBed.configureTestingModule({
      imports: [LessonEditorComponent],
      providers: [
        provideNoopAnimations(),
        { provide: LessonsService, useValue: lessonsService },
        { provide: MessageService, useValue: messageService },
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(LessonEditorComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('loading', false);
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  describe('lesson display', () => {
    it('should ask the user to select a lesson when no lesson is loaded', () => {
      fixture.detectChanges();

      expect(fixture.nativeElement.textContent).toContain('Select a lesson');
    });

    it('should display the lesson title when a lesson is loaded', () => {
      fixture.componentRef.setInput('lesson', mockLesson);
      fixture.detectChanges();

      expect(fixture.nativeElement.textContent).toContain('Introduction');
    });

    it('should display a loading skeleton while loading a lesson', () => {
      fixture.componentRef.setInput('loading', true);
      fixture.detectChanges();

      expect(fixture.nativeElement.querySelector('p-skeleton')).toBeTruthy();
    });

    it('should display a message when the lesson has no title', () => {
      fixture.componentRef.setInput('lesson', { ...mockLesson, title: '' });
      fixture.detectChanges();

      expect(fixture.nativeElement.textContent).toContain('No data available');
    });
  });

  describe('text changes', () => {
    it('should emit updated content when the change comes from the user', () => {
      fixture.detectChanges();
      spyOn(component.onCourseContentChanged, 'emit');

      component.handleTextChange({
        source: 'user',
        htmlValue: '<p>Updated</p>',
        textValue: 'Updated',
        delta: '',
      });

      expect(component.onCourseContentChanged.emit).toHaveBeenCalledWith('<p>Updated</p>');
    });

    it('should not emit updated content when the change comes from the API', () => {
      fixture.detectChanges();
      spyOn(component.onCourseContentChanged, 'emit');

      component.handleTextChange({
        source: 'api',
        htmlValue: '<p>Updated</p>',
        textValue: 'Updated',
        delta: '',
      });

      expect(component.onCourseContentChanged.emit).not.toHaveBeenCalled();
    });
  });

  describe('save lesson', () => {
    it('should not save when no lesson is loaded', () => {
      fixture.detectChanges();

      component.handleSaveLesson();

      expect(lessonsService.updateLessonContent).not.toHaveBeenCalled();
    });

    it('should not save when lesson content is empty', () => {
      fixture.componentRef.setInput('lesson', { ...mockLesson, content: '' });
      fixture.detectChanges();

      component.handleSaveLesson();

      expect(lessonsService.updateLessonContent).not.toHaveBeenCalled();
    });

    it('should save the lesson content', () => {
      fixture.componentRef.setInput('lesson', mockLesson);
      fixture.detectChanges();

      component.handleSaveLesson();

      expect(lessonsService.updateLessonContent).toHaveBeenCalledWith(
        'lesson-1',
        '<p>Welcome</p>',
      );
    });

    it('should update the lesson with the saved response', () => {
      const savedLesson = { ...mockLesson, content: '<p>Saved content</p>' };
      lessonsService.updateLessonContent.and.returnValue(of(savedLesson));
      fixture.componentRef.setInput('lesson', mockLesson);
      fixture.detectChanges();

      component.handleSaveLesson();

      expect(component.lesson).toEqual(savedLesson);
    });
  });
});
