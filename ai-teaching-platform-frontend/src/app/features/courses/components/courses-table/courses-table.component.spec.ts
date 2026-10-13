import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CoursesTableComponent } from './courses-table.component';
import {CoursesService} from '../../services/courses.service';
import {MessageService} from 'primeng/api';
import {of} from 'rxjs';
import {Course} from '../../models/course.model';
import {provideRouter, Router} from '@angular/router';
import {provideNoopAnimations} from '@angular/platform-browser/animations';

describe('CoursesTableComponent', () => {
  let component: CoursesTableComponent;
  let fixture: ComponentFixture<CoursesTableComponent>;
  let coursesService: jasmine.SpyObj<CoursesService>;
  let messageService: jasmine.SpyObj<MessageService>;

  beforeEach(async () => {
    coursesService = jasmine.createSpyObj(
      'CoursesService',
      ['deleteCourse']
    )

    messageService = jasmine.createSpyObj(
      'MessageService',
      ['add']
    )

    coursesService.deleteCourse.and.returnValue(of(void 0));

    await TestBed.configureTestingModule({
      imports: [CoursesTableComponent],
      providers:[
        provideRouter([]),
        provideNoopAnimations(),
        {
          provide: CoursesService,
          useValue: coursesService
        },
        {
          provide: MessageService,
          useValue: messageService
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CoursesTableComponent);
    component = fixture.componentInstance;

    component.maxRows = 10;
    component.loading = false;
    component.courses = [
      {id:'1',title:'Java',content:'Java'},
      {id:'2',title:'Angular',content:'Angular'},
      {id:'3',title:'Spring Boot',content:'Spring Boot'},
    ] as Course[];

    console.log('Rows:',
      fixture.nativeElement.querySelectorAll('tbody tr').length
    );

    console.log('Links:',
      fixture.nativeElement.querySelectorAll('a').length
    );

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the courses rows',()=>{
    const rows: NodeListOf<HTMLTableRowElement> =
      fixture.nativeElement.querySelectorAll(
        'tbody tr'
      );

    expect(rows.length).toBe(3);
    expect(rows[0].textContent).toContain('Java');
    expect(rows[1].textContent).toContain('Angular');
    expect(rows[2].textContent).toContain('Spring Boot');

  })

  it('should display an empty message when there are no courses', ()=>{
    component.courses = []

    fixture.detectChanges();

    const text =
      fixture.nativeElement.textContent;

    expect(text).toContain('No courses found');

  })

  it('should display skeleton while loading',()=>{

    component.loading = true;

    fixture.detectChanges();

    const skeletons =
      fixture.nativeElement.querySelectorAll('p-skeleton');

    expect(skeletons.length).toBe(component.courses.length);
    expect(fixture.nativeElement.textContent).not.toContain('Java');
    expect(fixture.nativeElement.textContent).not.toContain('Angular');
    expect(fixture.nativeElement.textContent).not.toContain('Spring Boot');

  })

  it("should call the deleteCourse with the correct ID",()=>{
    component.onDeleteCourse('2')

    expect(coursesService.deleteCourse)
      .toHaveBeenCalledWith('2')
  })

  it("should display a success message after deletion",()=>{
    component.onDeleteCourse('2')
    expect(messageService.add).toHaveBeenCalledWith({
      severity: 'success',
      summary: 'Course deleted successfully'
    });
  })

  it('should emit courseDeleted after successful deletion',()=>{
    spyOn(component.courseDeleted, 'emit');

    component.onDeleteCourse('2')

    expect(component.courseDeleted.emit).toHaveBeenCalled();
  })

  it('should call deleteCourse when the trash button is clicked',()=>{
    const deleteButton: HTMLButtonElement =
      fixture.nativeElement.querySelector('[data-testid="delete-course"] button');

    deleteButton.click();

    expect(coursesService.deleteCourse).toHaveBeenCalledWith('1');
  })

  it('should show at most maxRows rows per page',()=>{
    component.courses = Array.from({length: 12},(_, i)=>({
      id: `${i}`, title: `Course ${i}`, content:''
    })) as Course[];

    fixture.detectChanges();

    const rows = fixture.nativeElement.querySelectorAll('tbody tr');
    expect(rows.length).toBe(component.maxRows);
  })

});
