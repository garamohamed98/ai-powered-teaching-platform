import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AttemptResultComponent } from './attempt-result.component';

describe('AttemptResultComponent', () => {
  let component: AttemptResultComponent;
  let fixture: ComponentFixture<AttemptResultComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AttemptResultComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AttemptResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
