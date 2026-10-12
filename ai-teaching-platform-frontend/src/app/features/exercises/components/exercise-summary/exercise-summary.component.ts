import {Component, Input} from '@angular/core';
import {Card} from 'primeng/card';
import {Exercise, getExerciseTypeLabel} from '../../models/exercise.model';
import {Lesson} from '../../models/lesson.model';

@Component({
  selector: 'app-exercise-summary',
  imports: [
    Card
  ],
  templateUrl: './exercise-summary.component.html',
  styleUrl: './exercise-summary.component.scss'
})
export class ExerciseSummaryComponent {

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
