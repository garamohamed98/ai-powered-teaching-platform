import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MultipleChoiceFormComponent } from './multiple-choice-form.component';
import {FormControl, FormGroup} from '@angular/forms';

describe('MultipleChoiceFormComponent', () => {
  let component: MultipleChoiceFormComponent;
  let fixture: ComponentFixture<MultipleChoiceFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MultipleChoiceFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MultipleChoiceFormComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput(
      'form',
      new FormGroup({
        question: new FormControl(''),
        options: new FormControl([]),
        correctAnswer: new FormControl('')
      })
    )
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
