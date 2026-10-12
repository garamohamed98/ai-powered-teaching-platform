import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MultipleChoiceAttemptResultComponent } from './multiple-choice-attempt-result.component';

describe('MultipleChoiceAttemptResultComponent', () => {
  let component: MultipleChoiceAttemptResultComponent;
  let fixture: ComponentFixture<MultipleChoiceAttemptResultComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MultipleChoiceAttemptResultComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MultipleChoiceAttemptResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
