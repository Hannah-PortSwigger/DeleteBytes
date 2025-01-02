import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

@SuppressWarnings("unused")
public class Extension implements BurpExtension
{
    public static final String EXTENSION_NAME = "Delete bytes";

    @Override
    public void initialize(MontoyaApi montoyaApi)
    {
        montoyaApi.extension().setName(EXTENSION_NAME);

        montoyaApi.userInterface().registerContextMenuItemsProvider(new MyContextMenuItemsProvider(montoyaApi.logging(), montoyaApi.userInterface().swingUtils().suiteFrame()));
    }
}