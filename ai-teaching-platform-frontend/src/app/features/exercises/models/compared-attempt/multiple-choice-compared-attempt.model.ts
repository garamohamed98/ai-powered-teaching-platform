export interface MultipleChoiceComparedAttempt{
  question: string;
  options: string[];
  correctAnswer: string;
  submittedAnswers: string;
  isCorrect: boolean;
}
