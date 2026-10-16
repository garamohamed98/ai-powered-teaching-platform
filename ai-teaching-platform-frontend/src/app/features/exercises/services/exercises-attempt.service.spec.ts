import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { ExercisesAttemptService } from './exercises-attempt.service';

describe('ExercisesAttemptService', () => {
  let service: ExercisesAttemptService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient()]
    });
    service = TestBed.inject(ExercisesAttemptService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
