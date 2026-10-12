export interface MultipleChoiceComparedAttempt{
  question: string;
  options: string[];
  correctAnswers: string;
  submittedAnswer: string;
  isCorrect: boolean;
}
