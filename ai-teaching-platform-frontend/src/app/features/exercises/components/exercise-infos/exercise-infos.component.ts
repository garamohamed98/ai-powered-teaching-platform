import {Component, Input} from '@angular/core';
import {Card} from 'primeng/card';
import {Exercise, getExerciseTypeLabel} from '../../models/exercise.model';
import {Lesson} from '../../models/lesson.model';

@Component({
  selector: 'app-exercise-infos',
  imports: [
    Card
  ],
  templateUrl: './exercise-infos.component.html',
  styleUrl: './exercise-infos.component.scss'
})
export class ExerciseInfosComponent {

  @Input({required:true}) exercise!: Exercise | null;

  getLessonListAsString(lessons: Lesson[]):string {
    console.log("those the lessons: ", lessons);
    return lessons.map((lesson:Lesson) => {
      console.log("title of the lesson:", lesson.title);
      return lesson.title
    }).join(', ');
  }

  protected readonly getExerciseTypeLabel = getExerciseTypeLabel;
}
