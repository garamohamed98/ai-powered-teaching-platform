import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { LessonsPanelComponent } from './lessons-panel.component';
import { Lesson } from '../../models/lesson.model';

describe('LessonsPanelComponent', () => {
  let component: LessonsPanelComponent;
  let fixture: ComponentFixture<LessonsPanelComponent>;

  const mockLessons: Lesson[] = [
    { id: '1', title: 'Introduction', course_id: 'course-1', content: 'Welcome' },
    { id: '2', title: 'Advanced topics', course_id: 'course-1', content: 'Details' },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LessonsPanelComponent],
    })
      .compileComponents();

    fixture = TestBed.createComponent(LessonsPanelComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('lessons', mockLessons);
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  describe('lessons list', () => {
    it('should pass the lessons to the listbox', () => {
      fixture.detectChanges();
      const listbox = fixture.debugElement.query(By.css('p-listbox'));

      expect(listbox.componentInstance.options).toEqual(mockLessons);
    });

    it('should display lesson titles in the listbox', () => {
      fixture.detectChanges();

      expect(fixture.nativeElement.textContent).toContain('Introduction');
      expect(fixture.nativeElement.textContent).toContain('Advanced topics');
    });
  });

  describe('lesson selection', () => {
    it('should emit lessonSelected with the selected lesson', () => {
      fixture.detectChanges();
      spyOn(component.lessonSelected, 'emit');
      const listbox = fixture.debugElement.query(By.css('p-listbox'));

      listbox.triggerEventHandler('onChange', { value: mockLessons[0] });

      expect(component.lessonSelected.emit).toHaveBeenCalledWith(mockLessons[0]);
    });
  });
});
