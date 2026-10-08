import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FillInBlankAttemptFormComponent } from './fill-in-blank-attempt-form.component';

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
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
