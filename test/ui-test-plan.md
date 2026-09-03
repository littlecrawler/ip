# UI Test Plan

This plan checks Noah's observable command-line behavior. Each test case starts
with an empty task list in a fresh process. Expected output blocks correspond
one-to-one with the commands and must appear exactly in the same order.

The banner and separator lines are displayed in the transcript but are omitted
from expected blocks because they do not describe command behavior.

Run the complete plan from the repository root:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .codex/skills/test-ui/scripts/run-ui-tests.ps1
```

## Test cases

### Add and list all task types

Aim: Verify that todo, deadline, and event commands create correctly formatted
tasks and that `list` preserves their order.

### Mark and unmark an inherited task

Aim: Verify that status changes inherited from `Task` are reflected through
polymorphic deadline output.

### Accept tasks without date delimiters

Aim: Verify the intended fallback constructors when deadline or event timing
information is omitted.

## Machine-readable cases

Keep this JSON block synchronized with the descriptions above. The
`test-ui` runner treats it as the executable source of truth.

<!-- TEST-CASES-START -->
```json
[
  {
    "name": "add-and-list-task-types",
    "aim": "Add and list todo, deadline, and event tasks in order.",
    "commands": [
      "todo borrow book",
      "deadline return book /by Sunday",
      "event project meeting /from Mon 2pm /to 4pm",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "Got it. I've added this task:\n  [T][ ] borrow book\nNow you have 1 tasks in the list.",
      "Got it. I've added this task:\n  [D][ ] return book (by: Sunday)\nNow you have 2 tasks in the list.",
      "Got it. I've added this task:\n  [E][ ] project meeting (from: Mon 2pm to: 4pm)\nNow you have 3 tasks in the list.",
      "Here are the tasks in your list:\n1.[T][ ] borrow book\n2.[D][ ] return book (by: Sunday)\n3.[E][ ] project meeting (from: Mon 2pm to: 4pm)",
      "Farewell, Traveler!\nHope to see you again soon."
    ]
  },
  {
    "name": "mark-and-unmark-deadline",
    "aim": "Update inherited completion state and preserve deadline details.",
    "commands": [
      "deadline return book /by Sunday",
      "mark 1",
      "unmark 1",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "Got it. I've added this task:\n  [D][ ] return book (by: Sunday)\nNow you have 1 tasks in the list.",
      "Nice! I've marked this task as done:\n  [D][X] return book (by: Sunday)",
      "OK, I've marked this task as not done yet:\n  [D][ ] return book (by: Sunday)",
      "Here are the tasks in your list:\n1.[D][ ] return book (by: Sunday)",
      "Farewell, Traveler!\nHope to see you again soon."
    ]
  },
  {
    "name": "tasks-without-date-delimiters",
    "aim": "Use fallback constructors when timing delimiters are absent.",
    "commands": [
      "deadline return book",
      "event project meeting",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "Got it. I've added this task:\n  [D][ ] return book\nNow you have 1 tasks in the list.",
      "Got it. I've added this task:\n  [E][ ] project meeting\nNow you have 2 tasks in the list.",
      "Here are the tasks in your list:\n1.[D][ ] return book\n2.[E][ ] project meeting",
      "Farewell, Traveler!\nHope to see you again soon."
    ]
  }
]
```
<!-- TEST-CASES-END -->
