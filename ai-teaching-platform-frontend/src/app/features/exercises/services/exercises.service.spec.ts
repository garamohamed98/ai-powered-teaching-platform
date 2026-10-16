import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { ExercisesService } from './exercises.service';

describe('ExercisesService', () => {
  let service: ExercisesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient()]
    });
    service = TestBed.inject(ExercisesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
