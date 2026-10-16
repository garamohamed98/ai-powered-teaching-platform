import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

import { LessonsTableComponent } from './lessons-table.component';

describe('LessonsTableComponent', () => {
  let component: LessonsTableComponent;
  let fixture: ComponentFixture<LessonsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LessonsTableComponent],
      providers: [provideNoopAnimations()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LessonsTableComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput("lessons",[])
    fixture.componentRef.setInput("loading",false)
    fixture.componentRef.setInput("maxRows",5)
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
