import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CoursesFormModalComponent } from './courses-form-modal.component';
import {CoursesService} from '../../services/courses.service';
import {MessageService} from 'primeng/api';
import {provideNoopAnimations} from '@angular/platform-browser/animations';
import {of} from 'rxjs';

describe('CoursesFormModalComponent', () => {
  let component: CoursesFormModalComponent;
  let fixture: ComponentFixture<CoursesFormModalComponent>;
  let coursesService: jasmine.SpyObj<CoursesService>;
  let messageService: jasmine.SpyObj<MessageService>;

  beforeEach(async () => {
    coursesService = jasmine.createSpyObj(
      'CoursesService',
      ['createCourse']
    )

    messageService = jasmine.createSpyObj(
      'MessageService',
      ['add']
    )

    coursesService.createCourse.and.returnValue(of({
      id:"1",
      title:"Ai",
      content:"AI"
    }))

    await TestBed.configureTestingModule({
      imports: [CoursesFormModalComponent],
      providers:[
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

    fixture = TestBed.createComponent(CoursesFormModalComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  describe('form validation', () => {

    it('should be invalid when the title is empty',()=>{
      fixture.detectChanges();
      component.form.setValue({title: ''})

      expect(component.form.valid).toBeFalse();
    })

    it('should be invalid when the title has less than 3 characters',()=>{
      fixture.detectChanges();
      component.form.setValue({title:'Ab'})

      expect(component.form.valid).toBeFalse();
    })

    it('should be valid when the title has 3 or more characters',()=>{
      fixture.detectChanges();
      component.form.setValue({title:'Angular'})

      expect(component.form.valid).toBeTrue();
    })

    it('should not show the validation message at first',()=>{
      fixture.detectChanges();

      expect(document.body.textContent)
        .not.toContain('More than 3 characters in course title is required');
    })

    it('should show the validation message when the title is touched and invalid',()=>{
      fixture.componentRef.setInput('isVisible', true);

      const titleControl = component.form.get('title');
      titleControl?.markAsTouched();

      fixture.detectChanges();

      expect(fixture.nativeElement.textContent)
        .toContain('More then 3 character in course title is required');
    })
  })

  describe('submit',()=>{
    it('should not call createCourse when the form is invalid',()=>{
      fixture.detectChanges();
      component.form.setValue({title:''})

      component.submit();

      expect(coursesService.createCourse).not.toHaveBeenCalled();
    })

    it('should call createCourse with the title',()=>{
      fixture.detectChanges();
      component.form.setValue({title:'Angular'});

      component.submit();

      expect(coursesService.createCourse).toHaveBeenCalledWith('Angular');
    });

    it('should display a success message after creation',()=>{
      fixture.detectChanges();
      component.form.setValue({title:'Angular'});

      component.submit();

      expect(messageService.add).toHaveBeenCalledWith(
        jasmine.objectContaining({severity: 'success'})
      )
    })

    it('should emit courseCreated after creation',()=>{
      fixture.detectChanges();
      spyOn(component.courseCreated, 'emit');
      component.form.setValue({title: 'Angular'});

      component.submit();

      expect(component.courseCreated.emit).toHaveBeenCalled();
    })

    it('should close the modal after creation',()=>{
      fixture.detectChanges();
      spyOn(component.isVisibleChange, 'emit');
      component.form.setValue({title:'Angular'});

      component.submit();

      expect(component.isVisible).toBeFalse();
      expect(component.isVisibleChange.emit).toHaveBeenCalledWith(false);
    })
  })

  describe('close',()=>{
    it('should emit isVisibleChange with false when closed',()=>{
      fixture.detectChanges();
      spyOn(component.isVisibleChange, 'emit');

      component.close();

      expect(component.isVisible).toBeFalse();
      expect(component.isVisibleChange.emit).toHaveBeenCalledWith(false);
    })
  })

});
