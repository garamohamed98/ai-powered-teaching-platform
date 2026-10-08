import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Exercise} from '../models/exercise.model';
import {CreateExerciseDto} from '../models/create-exercise.dto';
import {GenerateExerciseDto} from '../models/generate-exercise.dto';

@Injectable({
  providedIn: 'root'
})
export class ExercisesService {

  private http = inject(HttpClient)
  private baseUrl = 'http://localhost:8080/api/exercise';

  constructor() { }

  getExercises(courseId: string) {
    console.log("Fetch exercises");
    return this.http.get<Exercise[]>(this.baseUrl + `/course/${courseId}`);
  }

  createExercise(createExerciseDto: CreateExerciseDto) {
    return this.http.post<Exercise>(this.baseUrl, createExerciseDto);
  }

  generateExercise(generateExerciseDto: GenerateExerciseDto) {
    return this.http.post<Exercise>(this.baseUrl+ "/generate", generateExerciseDto);
  }

  getExerciseById(exerciseId: string) {
    console.log("Fetch exercise by id: " + exerciseId);
    return this.http.get<Exercise>(`${this.baseUrl}/${exerciseId}`);
  }

}
