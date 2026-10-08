import { ComponentFixture, TestBed } from '@angular/core/testing';

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
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
