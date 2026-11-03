import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CoursesTableComponent} from './courses-table.component';
import {CoursesService} from '../../services/courses.service';
import {MessageService} from 'primeng/api';
import {provideRouter, RouterLink} from '@angular/router';
import {provideNoopAnimations} from '@angular/platform-browser/animations';
import {By} from '@angular/platform-browser';
import {of, throwError} from 'rxjs';
import {Course} from '../../models/course.model';


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
        provideRouter([]),
        provideNoopAnimations()
      ]
    })
      .compileComponents();

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
    })

    it('should show course title', () => {
      const firstRow = getRows()[0];

      expect(firstRow.nativeElement.textContent).toContain(MOCK_COURSES[0].title);
    })

    it('should show course category name', () => {
      const cells = getCells().map(c => c.nativeElement.textContent.trim());

      expect(cells).toContain('Frontend');
    })

    it('should show course visibility', () => {
      const cells = getCells().map(c => c.nativeElement.textContent.trim());

      expect(cells).toContain('PUBLIC');
    })

    it('should show the creation date formatted as dd MMM yyyy, HH:mm', () => {
      const cells = getCells().map(c => c.nativeElement.textContent.trim());

      expect(cells).toContain('15 Jan 2025, 10:30');
    })

    it('should show a navigation button and a delete button for each row', () => {
      const rows = getRows();

      expect(rows.length).toBe(MAX_ROWS);
      rows.forEach(row => {
        expect(row.queryAll(By.css('p-button')).length).toBe(2);
        expect(row.query(By.css('[data-testid="delete-course"]'))).not.toBeNull();
      });
    })

    it('should link the navigation button to the course details', () => {
      const navButton = getRows()[0].queryAll(By.css('p-button'))[0];
      const routerLink = navButton.injector.get(RouterLink);

      expect(routerLink.urlTree?.toString()).toBe(`/${MOCK_COURSES[0].id}/course-details`);
    })

    it('should not show the empty message', () => {
      expect(getEmptyMessage()).toBeNull();
    })

    it('should not show skeleton', () => {
      expect(getSkeletons().length).toBe(0);
    })

  })

  describe('when the list is paginated', () => {

    it('should only render up to maxRows rows per page', () => {
      setInputs({courses: MOCK_COURSES, maxRows: MAX_ROWS});

      expect(getRows().length).toBe(MAX_ROWS);
    })

    it('should render all rows when maxRows is bigger than the list', () => {
      setInputs({courses: MOCK_COURSES, maxRows: 10});

      expect(getRows().length).toBe(MOCK_COURSES.length);
    })

    it('should show the paginator', () => {
      setInputs({courses: MOCK_COURSES});

      expect(getPaginator()).not.toBeNull();
    })

    it('should show the second page when next page is clicked', () => {
      setInputs({courses: MOCK_COURSES, maxRows: MAX_ROWS});

      fixture.debugElement.query(By.css('.p-paginator-next')).nativeElement.click();
      fixture.detectChanges();

      const rows = getRows();
      expect(rows.length).toBe(1);
      expect(rows[0].nativeElement.textContent).toContain(MOCK_COURSES[2].title);
    })

  })

  describe('when there are no courses', () => {

    beforeEach(() => {
      setInputs({courses: [], loading: false});
    })

    it('should show the empty message', () => {
      const message = getEmptyMessage();

      expect(message).not.toBeNull();
      expect(message.nativeElement.textContent).toContain('No courses found.');
    })

    it('should not show any course rows', () => {
      expect(getRows().length).toBe(0);
    })

    it('should not show skeleton', () => {
      expect(getSkeletons().length).toBe(0);
    })

  })

  describe('when courses are loading', () => {

    beforeEach(() => {
      setInputs({courses: MOCK_COURSES, loading: true});
    })

    it('should show skeletons for every cell of every row', () => {
      expect(getSkeletons().length).toBe(MAX_ROWS * SKELETONS_PER_ROW);
    })

    it('should not show course title', () => {
      const text = fixture.nativeElement.textContent;

      expect(text).not.toContain(MOCK_COURSES[0].title);
    })

    it('should not show course category, visibility or creation date', () => {
      const text = fixture.nativeElement.textContent;

      expect(text).not.toContain('Frontend');
      expect(text).not.toContain('PUBLIC');
      expect(text).not.toContain('15 Jan 2025, 10:30');
    })

    it('should not show the empty message', () => {
      expect(getEmptyMessage()).toBeNull();
    })

  })

  describe('delete button', () => {

    const clickDeleteButton = (rowIndex = 0) => {
      fixture.detectChanges();

      getRows()[rowIndex].query(
        By.css('[data-testid="delete-course"] button')
      )
        .nativeElement.click();

      fixture.detectChanges();
    }

    beforeEach(() => {
      setInputs({courses: MOCK_COURSES, loading: false});
    })

    describe('when deletion succeeds', () => {

      beforeEach(() => {
        coursesService.deleteCourse.and.returnValue(of(void 0) as any);
      })

      it('should call deleteCourse with the course id', () => {
        clickDeleteButton(0);

        expect(coursesService.deleteCourse).toHaveBeenCalledOnceWith(MOCK_COURSES[0].id);
      })

      it('should call deleteCourse with the id of the clicked row', () => {
        clickDeleteButton(1);

        expect(coursesService.deleteCourse).toHaveBeenCalledOnceWith(MOCK_COURSES[1].id);
      })

      it('should show a success message', () => {
        clickDeleteButton();

        expect(messageService.add).toHaveBeenCalledOnceWith({
          severity: 'success',
          summary: 'Course deleted successfully',
        });
      })

      it('should emit courseDeleted', () => {
        const emitSpy = spyOn(component.courseDeleted, 'emit');

        clickDeleteButton();

        expect(emitSpy).toHaveBeenCalledTimes(1);
      })

    })

    describe('when deletion fails', () => {

      beforeEach(() => {
        coursesService.deleteCourse.and.returnValue(throwError(() => new Error('Server error')));
      })

      it('should not show a success message', () => {
        clickDeleteButton();

        expect(messageService.add).not.toHaveBeenCalled();
      })

      it('should not emit courseDeleted', () => {
        const emitSpy = spyOn(component.courseDeleted, 'emit');

        clickDeleteButton();

        expect(emitSpy).not.toHaveBeenCalled();
      })

    })

  })

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

});
