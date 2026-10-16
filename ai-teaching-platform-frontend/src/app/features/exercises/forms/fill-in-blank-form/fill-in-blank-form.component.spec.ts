import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormArray, FormControl, FormGroup } from '@angular/forms';

import { FillInBlankFormComponent } from './fill-in-blank-form.component';

describe('FillInBlankFormComponent', () => {
  let component: FillInBlankFormComponent;
  let fixture: ComponentFixture<FillInBlankFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FillInBlankFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FillInBlankFormComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('form', new FormGroup({
      sentences: new FormArray([])
    }));
    fixture.componentRef.setInput('createSentence', () => new FormGroup({
      question: new FormControl(''),
      answers: new FormControl([])
    }));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
