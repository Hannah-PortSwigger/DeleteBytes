import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

import static burp.api.montoya.core.ByteArray.byteArray;
import static burp.api.montoya.http.message.requests.HttpRequest.httpRequest;
import static burp.api.montoya.http.message.responses.HttpResponse.httpResponse;
import static burp.api.montoya.ui.contextmenu.InvocationType.*;
import static java.lang.Integer.parseInt;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.showInputDialog;

public class MyContextMenuItemsProvider implements ContextMenuItemsProvider
{
    private final Logging logging;
    private final Frame suiteFrame;

    public MyContextMenuItemsProvider(Logging logging, Frame frame)
    {
        this.logging = logging;
        this.suiteFrame = frame;
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event)
    {
        if (event.isFrom(MESSAGE_EDITOR_REQUEST, MESSAGE_EDITOR_RESPONSE))
        {
            JMenuItem menuItem = new JMenuItem("Delete bytes...");
            menuItem.addActionListener(l -> {
                try {
                    int numberOfBytes = parseInt(showInputDialog(
                            suiteFrame,
                            "Enter number of bytes to delete",
                            "Delete bytes",
                            QUESTION_MESSAGE
                    ));

                    int startOffset = event.messageEditorRequestResponse().get().caretPosition() - 1;

                    HttpRequestResponse httpRequestResponse = event.messageEditorRequestResponse().get().requestResponse();

                    if (event.isFrom(MESSAGE_EDITOR_REQUEST))
                    {
                        ByteArray newByteArray = constructByteArray(httpRequestResponse.request().toByteArray(), startOffset, numberOfBytes);

                        HttpRequest request = httpRequest(httpRequestResponse.request().httpService(), newByteArray);
                        request = request.withBody(request.body());

                        event.messageEditorRequestResponse().get().setRequest(request);
                    }
                    else if (event.isFrom(MESSAGE_EDITOR_RESPONSE))
                    {
                        ByteArray newByteArray = constructByteArray(httpRequestResponse.response().toByteArray(), startOffset, numberOfBytes);

                        HttpResponse response = httpResponse(newByteArray);
                        response = response.withBody(response.body());

                        event.messageEditorRequestResponse().get().setResponse(response);
                    }
                }
                catch (NumberFormatException e)
                {
                    logging.logToError("Please provide an integer value.");
                }
            });

            return List.of(menuItem);
        }

        return Collections.emptyList();
    }

    private static ByteArray constructByteArray(ByteArray input, int startOffset, int numberOfBytes)
    {
        ByteArray prefixByteArray = input.subArray(0, startOffset);
        ByteArray suffixByteArray = startOffset + numberOfBytes >= input.length()
                ? byteArray()
                : input.subArray(startOffset + numberOfBytes, input.length());

        return prefixByteArray.withAppended(suffixByteArray);
    }
}
