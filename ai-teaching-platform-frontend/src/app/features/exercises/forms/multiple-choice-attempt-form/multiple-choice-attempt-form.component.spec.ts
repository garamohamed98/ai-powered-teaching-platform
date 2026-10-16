import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup } from '@angular/forms';

import { MultipleChoiceAttemptFormComponent } from './multiple-choice-attempt-form.component';

describe('MultipleChoiceAttemptFormComponent', () => {
  let component: MultipleChoiceAttemptFormComponent;
  let fixture: ComponentFixture<MultipleChoiceAttemptFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MultipleChoiceAttemptFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MultipleChoiceAttemptFormComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('content', {
      question: 'What is Java?',
      options: ['A language', 'A database'],
      correctAnswer: 'A language'
    });
    fixture.componentRef.setInput('form', new FormGroup({
      answer: new FormControl('')
    }));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
