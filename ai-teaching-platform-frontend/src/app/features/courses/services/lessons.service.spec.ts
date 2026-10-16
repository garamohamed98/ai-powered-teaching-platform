import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { LessonsService } from './lessons.service';

describe('LessonsService', () => {
  let service: LessonsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient()]
    });
    service = TestBed.inject(LessonsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
