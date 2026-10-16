import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FillInBlankAttemptFormComponent } from './fill-in-blank-attempt-form.component';
import {FormArray, FormControl, FormGroup} from '@angular/forms';

describe('FillInBlankAttemptFormComponent', () => {
  let component: FillInBlankAttemptFormComponent;
  let fixture: ComponentFixture<FillInBlankAttemptFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FillInBlankAttemptFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FillInBlankAttemptFormComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput("content", {
      sentences: [
        { id: 1, text: 'Java is a programming language.' }
      ],
      answer: 'Java'
    });
    fixture.componentRef.setInput(
      'form',
      new FormGroup({
        sentences: new FormArray([
          new FormGroup({
            answer: new FormControl('')
          })
        ])
      })
    );
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
