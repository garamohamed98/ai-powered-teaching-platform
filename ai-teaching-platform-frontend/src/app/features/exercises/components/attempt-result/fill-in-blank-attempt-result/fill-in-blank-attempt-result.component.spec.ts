import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FillInBlankAttemptResultComponent } from './fill-in-blank-attempt-result.component';

describe('FillInBlankAttemptResultComponent', () => {
  let component: FillInBlankAttemptResultComponent;
  let fixture: ComponentFixture<FillInBlankAttemptResultComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FillInBlankAttemptResultComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FillInBlankAttemptResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
