import {ComponentFixture, TestBed} from '@angular/core/testing';
import {Component} from '@angular/core';

import {CoursesTableComponent} from './courses-table.component';
import {CoursesService} from '../../services/courses.service';
import {MessageService} from 'primeng/api';
import {provideRouter, Router} from '@angular/router';
import {provideNoopAnimations} from '@angular/platform-browser/animations';
import {By} from '@angular/platform-browser';
import {Course} from '../../models/course.model';
import {of, throwError} from 'rxjs';

@Component({template: ''})
class DummyComponent {
}

const MAX_ROWS = 2;
const SKELETONS_PER_ROW = 4;

const MOCK_COURSES = [
  {
    id: '1',
    title: 'Angular Basics',
    category: {name: 'Frontend'},
    visibility: 'PUBLIC',
    createdAt: new Date(2025, 0, 15, 10, 30),
  },
  {
    id: '2',
    title: 'Spring Boot Deep Dive',
    category: {name: 'Backend'},
    visibility: 'PRIVATE',
    createdAt: new Date(2025, 1, 20, 14, 5),
  },
  {
    id: '3',
    title: 'Docker for Beginners',
    category: {name: 'DevOps'},
    visibility: 'PUBLIC',
    createdAt: new Date(2025, 2, 3, 9, 0),
  },
] as unknown as Course[];


describe('CoursesTableComponent', () => {
  let component: CoursesTableComponent;
  let fixture: ComponentFixture<CoursesTableComponent>;
  let coursesService: jasmine.SpyObj<CoursesService>;
  let messageService: jasmine.SpyObj<MessageService>;

  const setInputs = (inputs: { courses?: Course[], loading?: boolean, maxRows?: number }) => {
    const {courses = [], loading = false, maxRows = MAX_ROWS} = inputs;
    fixture.componentRef.setInput('courses', courses);
    fixture.componentRef.setInput('loading', loading);
    fixture.componentRef.setInput('maxRows', maxRows);
    fixture.detectChanges();
  };

  const getRows = () => fixture.debugElement.queryAll(By.css('.title-row'));
  const getSkeletons = () => fixture.debugElement.queryAll(By.css('p-skeleton'));
  const getEmptyMessage = () => fixture.debugElement.query(By.css('p-message'));
  const getPaginator = () => fixture.debugElement.query(By.css('p-paginator'));
  const getCells = () => fixture.debugElement.queryAll(By.css('tbody td'));

  beforeEach(async () => {
    coursesService = jasmine.createSpyObj('CoursesService', ['deleteCourse']);
    messageService = jasmine.createSpyObj('MessageService', ['add']);

    await TestBed.configureTestingModule({
      imports: [CoursesTableComponent],
      providers: [
        {provide: CoursesService, useValue: coursesService},
        {provide: MessageService, useValue: messageService},
        provideRouter([{path: ':id/course-details', component: DummyComponent}]),
        provideNoopAnimations()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CoursesTableComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    setInputs({});
    expect(component).toBeTruthy();
  });

  it('should show the card header', () => {
    setInputs({courses: MOCK_COURSES});
    const card = fixture.debugElement.query(By.css('p-card'));

    expect(card.nativeElement.textContent).toContain('Courses List');
  });

  it('should show the table column headers', () => {
    setInputs({courses: MOCK_COURSES});
    const headers = fixture.debugElement.queryAll(By.css('th'))
      .map(th => th.nativeElement.textContent.trim());

    expect(headers).toEqual(['Title', 'Category', 'Visibility', 'Create At']);
  });

  describe('when there are courses', () => {

    beforeEach(() => {
      setInputs({courses: MOCK_COURSES, loading: false});
    });

    it('should show course title', () => {
      expect(getRows()[0].nativeElement.textContent).toContain(MOCK_COURSES[0].title);
    });

    it('should show course category name', () => {
      const cells = getCells().map(c => c.nativeElement.textContent.trim());

      expect(cells).toContain('Frontend');
    });

    it('should show course visibility', () => {
      const cells = getCells().map(c => c.nativeElement.textContent.trim());

      expect(cells).toContain('Public');
    });

    it('should show the creation date formatted as dd MMM yyyy, HH:mm', () => {
      const cells = getCells().map(c => c.nativeElement.textContent.trim());

      expect(cells).toContain('15 Jan 2025, 10:30');
    });

    it('should show the desktop and mobile menu buttons in each row', () => {
      const rows = getRows();

      expect(rows.length).toBe(MAX_ROWS);
      rows.forEach(row => {
        expect(row.queryAll(By.css('p-button')).length).toBe(2);
        expect(row.query(By.css('[data-testid="menu-button"]'))).not.toBeNull();
        expect(row.query(By.css('[data-testid="mobile-menu-button"]'))).not.toBeNull();
      });
    });

    it('should not show the empty message', () => {
      expect(getEmptyMessage()).toBeNull();
    });

    it('should not show skeleton', () => {
      expect(getSkeletons().length).toBe(0);
    });

  });

  describe('when the list is paginated', () => {

    it('should only render up to maxRows rows per page', () => {
      setInputs({courses: MOCK_COURSES, maxRows: MAX_ROWS});

      expect(getRows().length).toBe(MAX_ROWS);
    });

    it('should render all rows when maxRows is bigger than the list', () => {
      setInputs({courses: MOCK_COURSES, maxRows: 10});

      expect(getRows().length).toBe(MOCK_COURSES.length);
    });

    it('should show the paginator', () => {
      setInputs({courses: MOCK_COURSES});

      expect(getPaginator()).not.toBeNull();
    });

    it('should show the second page when next page is clicked', () => {
      setInputs({courses: MOCK_COURSES, maxRows: MAX_ROWS});

      fixture.debugElement.query(By.css('.p-paginator-next')).nativeElement.click();
      fixture.detectChanges();

      const rows = getRows();
      expect(rows.length).toBe(1);
      expect(rows[0].nativeElement.textContent).toContain(MOCK_COURSES[2].title);
    });

  });

  describe('when there are no courses', () => {

    beforeEach(() => {
      setInputs({courses: [], loading: false});
    });

    it('should show the empty message', () => {
      const message = getEmptyMessage();

      expect(message).not.toBeNull();
      expect(message.nativeElement.textContent).toContain('No courses found.');
    });

    it('should not show any course rows', () => {
      expect(getRows().length).toBe(0);
    });

    it('should not show skeleton', () => {
      expect(getSkeletons().length).toBe(0);
    });

  });

  describe('when courses are loading', () => {

    beforeEach(() => {
      setInputs({courses: MOCK_COURSES, loading: true});
    });

    it('should show skeletons for every cell of every row', () => {
      expect(getSkeletons().length).toBe(MAX_ROWS * SKELETONS_PER_ROW);
    });

    it('should not show course title', () => {
      expect(fixture.nativeElement.textContent).not.toContain(MOCK_COURSES[0].title);
    });

    it('should not show course category, visibility or creation date', () => {
      const text = fixture.nativeElement.textContent;

      expect(text).not.toContain('Frontend');
      expect(text).not.toContain('Public');
      expect(text).not.toContain('15 Jan 2025, 10:30');
    });

    it('should not show the empty message', () => {
      expect(getEmptyMessage()).toBeNull();
    });

  });

  describe('state transitions', () => {

    it('should go from skeletons to course data once loading finishes', () => {
      setInputs({courses: MOCK_COURSES, loading: true});
      expect(getSkeletons().length).toBe(MAX_ROWS * SKELETONS_PER_ROW);

      setInputs({courses: MOCK_COURSES, loading: false});

      expect(getSkeletons().length).toBe(0);
      expect(getRows()[0].nativeElement.textContent).toContain(MOCK_COURSES[0].title);
    });

    it('should go from the empty message to course rows when courses arrive', () => {
      setInputs({courses: [], loading: false});
      expect(getEmptyMessage()).not.toBeNull();

      setInputs({courses: MOCK_COURSES, loading: false});

      expect(getEmptyMessage()).toBeNull();
      expect(getRows().length).toBe(MAX_ROWS);
    });

    it('should go from course rows to the empty message when the list becomes empty', () => {
      setInputs({courses: MOCK_COURSES, loading: false});
      expect(getRows().length).toBe(MAX_ROWS);

      setInputs({courses: [], loading: false});

      expect(getRows().length).toBe(0);
      expect(getEmptyMessage()).not.toBeNull();
    });

  });

  describe('row menu', () => {

    const getMenuButton = (rowIndex = 0) =>
      getRows()[rowIndex].query(By.css('[data-testid="menu-button"] button'))
        .nativeElement as HTMLButtonElement;

    // the menu uses appendTo="body", so it lives in document.body, not in the fixture
    const getOverlay = () => document.body.querySelector('.p-menu-overlay');

    const getMenuItem = (label: string) =>
      Array.from(document.body.querySelectorAll<HTMLElement>('.p-menu-overlay .p-menu-item-link'))
        .find(el => el.textContent?.trim() === label);

    const settle = async () => {
      fixture.detectChanges();
      await fixture.whenStable();
      fixture.detectChanges();
    };

    const clickMenuButton = async (rowIndex = 0) => {
      getMenuButton(rowIndex).click();
      await settle();
    };

    beforeEach(() => {
      setInputs({courses: MOCK_COURSES, loading: false});
    });

    afterEach(() => {
      document.body.querySelectorAll('.p-menu-overlay').forEach(el => el.remove());
    });

    it('should not show the menu initially', () => {
      expect(getOverlay()).toBeNull();
    });

    it('should show the menu when the menu button is clicked', async () => {
      await clickMenuButton();

      expect(getOverlay()).not.toBeNull();
      expect(getMenuItem('View')).toBeTruthy();
      expect(getMenuItem('Delete')).toBeTruthy();
    });

    it('should hide the menu when the menu button is clicked again', async () => {
      await clickMenuButton();
      expect(getOverlay()).not.toBeNull();

      await clickMenuButton();

      expect(getOverlay()).toBeNull();
    });

    it('should not navigate when only the menu button is clicked', async () => {
      const router = TestBed.inject(Router);

      await clickMenuButton(0);

      expect(router.url).toBe('/');
    });

    it('should navigate to the course details when View is clicked', async () => {
      const router = TestBed.inject(Router);
      await clickMenuButton(1);

      getMenuItem('View')!.click();
      await settle();

      expect(router.url).toBe(`/${MOCK_COURSES[1].id}/course-details`);
    });

    describe('when Edit is clicked', () => {
      it('should emit editCourseClicked with the correct course and close the menu', async () => {
        const emitSpy = spyOn(component.editCourseClicked, 'emit');
        await clickMenuButton(1);

        expect(getMenuItem('Edit')).toBeTruthy();
        getMenuItem('Edit')!.click();
        await settle();

        expect(emitSpy).toHaveBeenCalledOnceWith(MOCK_COURSES[1]);
        expect(getOverlay()).toBeNull();
      });
    });

    describe('when Delete is clicked', () => {

      beforeEach(() => {
        coursesService.deleteCourse.and.returnValue(of(void 0) as any);
      });

      it('should delete the course of the row whose menu was opened', async () => {
        await clickMenuButton(1);

        getMenuItem('Delete')!.click();
        await settle();

        expect(coursesService.deleteCourse).toHaveBeenCalledOnceWith(MOCK_COURSES[1].id);
      });

      it('should show a success message and emit courseDeleted', async () => {
        const emitSpy = spyOn(component.courseDeleted, 'emit');
        await clickMenuButton(0);

        getMenuItem('Delete')!.click();
        await settle();

        expect(messageService.add).toHaveBeenCalledOnceWith({
          severity: 'success',
          summary: 'Course deleted successfully',
        });
        expect(emitSpy).toHaveBeenCalledTimes(1);
      });

      it('should close the menu after the item is clicked', async () => {
        await clickMenuButton(0);

        getMenuItem('Delete')!.click();
        await settle();

        expect(getOverlay()).toBeNull();
      });

    });

  });

});
