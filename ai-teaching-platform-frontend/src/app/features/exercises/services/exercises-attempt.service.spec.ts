import { TestBed } from '@angular/core/testing';

import { ExercisesAttemptService } from './exercises-attempt.service';

describe('ExercisesAttemptService', () => {
  let service: ExercisesAttemptService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ExercisesAttemptService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
