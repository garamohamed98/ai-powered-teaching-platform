import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MultipleChoiceContentComponent } from './multiple-choice-content.component';

describe('MultipleChoiceContentComponent', () => {
  let component: MultipleChoiceContentComponent;
  let fixture: ComponentFixture<MultipleChoiceContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MultipleChoiceContentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MultipleChoiceContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
